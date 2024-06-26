import java.util.HashSet;
import java.util.Set;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.infra.local.LocalAgArch;

public class MixedAgentArch extends AgArch {

    Set<String> jasonEnvActions = new HashSet<String>();

    @Override
    public void init() throws Exception {
        jasonEnvActions.add("open");
        jasonEnvActions.add("close");
        jasonEnvActions.add("get");
        jasonEnvActions.add("getBeer");
        jasonEnvActions.add("putBeer");
        jasonEnvActions.add("troughtBeer");
        jasonEnvActions.add("updateStock");
        jasonEnvActions.add("hand_in");
        jasonEnvActions.add("emptyBasket");
        jasonEnvActions.add("getDelivery");
        jasonEnvActions.add("sip");
        jasonEnvActions.add("has");
        jasonEnvActions.add("at");
        jasonEnvActions.add("takin");
        jasonEnvActions.add("move_towards"); 
        jasonEnvActions.add("deliver");
        jasonEnvActions.add("deliverReady");
        jasonEnvActions.add("doneAlianza");		
        jasonEnvActions.add("newAuction");
        // add other actions here
    }

    /** Send specific actions to Jason environment */
    @Override
    public void act(ActionExec act) {
        if (jasonEnvActions.contains(act.getActionTerm().getFunctor())) {
            getCentArch().act(act); // uses the local ag arch
        } else {
            super.act(act); // uses cartago ag arch
        }
    }

    protected LocalAgArch getCentArch() {
        AgArch arch = getTS().getAgArch().getFirstAgArch();
        while (arch != null) {
            if (arch instanceof LocalAgArch) {
                return (LocalAgArch)arch;
            }
            arch = arch.getNextAgArch();
        }
        return null;
    }
}
