package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.TemplateContract;
import com.template.states.IOUState;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import static net.corda.core.contracts.ContractsDSL.requireThat;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IOUFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class FlowInitiator extends FlowLogic<Void>{

        //private variables
        private final Integer iouValue ;
        private final Party otherParty;

        private final ProgressTracker progressTracker = new ProgressTracker();    


        //public constructor
        public FlowInitiator(Integer iouValue, Party otherParty) {
            this.iouValue = iouValue;
            this.otherParty = otherParty;
        }


        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }


        @Override
        @Suspendable
        public Void call() throws FlowException {
            
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            
            IOUState outputState = new IOUState(iouValue, getOurIdentity(), otherParty);
            Command command = new Command<>(new TemplateContract.Commands.Send(), getOurIdentity().getOwningKey());
 
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                        .addOutputState(outputState, TemplateContract.ID)
                        .addCommand(command);

            SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

            FlowSession otherPartySession = initiateFlow(otherParty);

            subFlow(new FinalityFlow(signedTx, otherPartySession));    

    
         return null;
    
        }
    }

    @InitiatedBy(FlowInitiator.class)
    public static class  IOUFlowResponder extends FlowLogic<Void>{
        //private variable
        private final FlowSession otherPartySession;

        //Constructor
        public  IOUFlowResponder(FlowSession otherPartySession) {
            this.otherPartySession = otherPartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            
            //Stored the transaction into data base.
            subFlow(new ReceiveFinalityFlow(otherPartySession));
            
            return null;
        }
    }

}
