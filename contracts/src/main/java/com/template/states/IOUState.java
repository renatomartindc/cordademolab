package com.template.states;

import com.template.contracts.TemplateContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(TemplateContract.class)
public class IOUState implements ContractState {

    //private variables
    private final int value;
    private final Party lender; //bob
    private final Party borrower; //alice

    /* Constructor of your Corda state */
    public  IOUState(int value, Party lender, Party borrower) {
        this.value = value;
        this.lender = lender;
        this.borrower = borrower;
    }

    //getters
    public int getValue() { return value; }
    public Party getLender() { return lender; }
    public Party getBorrower() { return borrower; }

    /* This method will indicate who are the participants and required signers when
     * this state is used in a transaction. */
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(lender,borrower);
    }
}