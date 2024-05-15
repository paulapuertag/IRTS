//package src.env;

import jaca.CartagoEnvironment;

import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.Location;
import jason.stdlib.string;
import jason.stdlib.structure;

import java.util.logging.Logger;
import java.io.StringReader;
import java.util.Set;

public class HouseEnv extends Environment {

    // common literals
    public static final Literal om = Literal.parseLiteral("open(medicalkit)");// for medication cabinet (to use instead of fridge)
    public static final Literal clm = Literal.parseLiteral("close(medicalkit)");
    public static final Literal of = Literal.parseLiteral("open(fridge)");
    public static final Literal clf = Literal.parseLiteral("close(fridge)");

    public static final Literal gm = Literal.parseLiteral("get(medication)");
    public static final Literal hm = Literal.parseLiteral("hand_in(medication)");
    public static final Literal sm = Literal.parseLiteral("taking(medication)");
    public static final Literal hom = Literal.parseLiteral("has(owner,Medication)");

    public static final Literal af = Literal.parseLiteral("at(robot,fridge)");
    public static final Literal am = Literal.parseLiteral("at(robot,medicalkit)");
    public static final Literal ao = Literal.parseLiteral("at(robot,owner)");
    public static final Literal ad = Literal.parseLiteral("at(robot,delivery)");

    public static final Literal aw = Literal.parseLiteral("at(robot,washer)");
    public static final Literal oaw = Literal.parseLiteral("at(owner,washer)");
    public static final Literal oaf = Literal.parseLiteral("at(owner,fridge)");
    public static final Literal oam = Literal.parseLiteral("at(owner,medicalkit)");
    public static final Literal oac1 = Literal.parseLiteral("at(owner,chair1)");
    public static final Literal oac2 = Literal.parseLiteral("at(owner,chair2)");
    public static final Literal oac3 = Literal.parseLiteral("at(owner,chair3)");
    public static final Literal oac4 = Literal.parseLiteral("at(owner,chair4)");
    public static final Literal oasf = Literal.parseLiteral("at(owner,sofa)");
    public static final Literal oad = Literal.parseLiteral("at(owner,delivery)");

    static Logger logger = Logger.getLogger(HouseEnv.class.getName());

    HouseModel model; // the model of the grid

    private CartagoEnvironment cartagoEnv;

    @Override
    public void init(String[] args) {
        model = new HouseModel();

        //if (args.length == 1 && args[0].equals("gui")) {
        HouseView view = new HouseView(model);
        model.setView(view);
        //}
        startCartago(args);

        updatePercepts();
    }

    void updateAgentsPlace() {
        // get the robot location
        Location lRobot = model.getAgPos(0);
        // get the robot room location
        String RobotPlace = model.getRoom(lRobot);
        addPercept("robot", Literal.parseLiteral("atRoom(" + RobotPlace + ")"));
        addPercept("owner", Literal.parseLiteral("atRoom(robot," + RobotPlace + ")"));
        // get the owner location
        Location lOwner = model.getAgPos(1);
        // get the owner room location
        String OwnerPlace = model.getRoom(lOwner);
        addPercept("owner", Literal.parseLiteral("atRoom(" + OwnerPlace + ")"));
        addPercept("robot", Literal.parseLiteral("atRoom(owner," + OwnerPlace + ")"));

        if (lRobot.distance(model.lDoorHome) == 0
                || lRobot.distance(model.lDoorKit1) == 0
                || lRobot.distance(model.lDoorKit2) == 0
                || lRobot.distance(model.lDoorSal1) == 0
                || lRobot.distance(model.lDoorSal2) == 0
                || lRobot.distance(model.lDoorBath1) == 0
                || lRobot.distance(model.lDoorBath2) == 0
                || lRobot.distance(model.lDoorBed1) == 0
                || lRobot.distance(model.lDoorBed2) == 0
                || lRobot.distance(model.lDoorBed3) == 0) {
            addPercept("robot", Literal.parseLiteral("atDoor"));
        };

        if (lOwner.distance(model.lDoorHome) == 0
                || lOwner.distance(model.lDoorKit1) == 0
                || lOwner.distance(model.lDoorKit2) == 0
                || lOwner.distance(model.lDoorSal1) == 0
                || lOwner.distance(model.lDoorSal2) == 0
                || lOwner.distance(model.lDoorBath1) == 0
                || lOwner.distance(model.lDoorBath2) == 0
                || lOwner.distance(model.lDoorBed1) == 0
                || lOwner.distance(model.lDoorBed2) == 0
                || lOwner.distance(model.lDoorBed3) == 0) {
            addPercept("owner", Literal.parseLiteral("atDoor"));
        };

    }

    void updateThingsPlace() {
        // get the medication cabinet location
        String medicationPlace = model.getRoom(model.lMedication);
        addPercept(Literal.parseLiteral("atRoom(medicalkit, " + medicationPlace + ")"));
        String fridgePlace = model.getRoom(model.lFridge);
        addPercept(Literal.parseLiteral("atRoom(fridge, " + fridgePlace + ")"));
        String sofaPlace = model.getRoom(model.lSofa);
        addPercept(Literal.parseLiteral("atRoom(sofa, " + sofaPlace + ")"));
        String chair1Place = model.getRoom(model.lChair1);
        addPercept(Literal.parseLiteral("atRoom(chair1, " + chair1Place + ")"));
        String chair2Place = model.getRoom(model.lChair2);
        addPercept(Literal.parseLiteral("atRoom(chair2, " + chair2Place + ")"));
        String chair3Place = model.getRoom(model.lChair3);
        addPercept(Literal.parseLiteral("atRoom(chair3, " + chair3Place + ")"));
        String chair4Place = model.getRoom(model.lChair4);
        addPercept(Literal.parseLiteral("atRoom(chair4, " + chair4Place + ")"));
        String deliveryPlace = model.getRoom(model.lDeliver);
        addPercept(Literal.parseLiteral("atRoom(delivery, " + deliveryPlace + ")"));
        String bed1Place = model.getRoom(model.lBed1);
        addPercept(Literal.parseLiteral("atRoom(bed1, " + bed1Place + ")"));
        String bed2Place = model.getRoom(model.lBed2);
        addPercept(Literal.parseLiteral("atRoom(bed2, " + bed2Place + ")"));
        String bed3Place = model.getRoom(model.lBed3);
        addPercept(Literal.parseLiteral("atRoom(bed3, " + bed3Place + ")"));
    }

    /**
     * creates the agents percepts based on the HouseModel
     */
    void updatePercepts() {
        // clear the percepts of the agents
        clearPercepts("robot");
        clearPercepts("owner");

        updateAgentsPlace();
        updateThingsPlace();

        Location lRobot = model.getAgPos(0);
        Location lOwner = model.getAgPos(1);

        if (lRobot.distanceChebyshev(model.lMedication) == 1) {
            addPercept("robot", am);
        }

        if (lOwner.distanceChebyshev(model.lMedication) == 1) {
            addPercept("owner", oam);
        }

        if (lRobot.distanceChebyshev(model.lFridge) == 1) {
            addPercept("robot", af);
        }

        if (lOwner.distanceChebyshev(model.lFridge) == 1) {
            addPercept("owner", oaf);
        }

        if (lRobot.distanceChebyshev(model.lWasher) == 1) {
            addPercept("robot", aw);
        }

        if (lOwner.distanceChebyshev(model.lWasher) == 1) {
            addPercept("owner", oaw);
        }

        if (lRobot.distanceChebyshev(lOwner) == 1) {
            addPercept("robot", ao);
        }

        if (lRobot.distanceChebyshev(model.lDeliver) == 1) {
            addPercept("robot", ad);
        }

        if (lOwner.distance(model.lChair1) == 0) {
            addPercept("owner", oac1);
            System.out.println("[owner] is at Chair1.");
        }

        if (lOwner.distance(model.lChair2) == 0) {
            addPercept("owner", oac2);
            System.out.println("[owner] is at Chair2.");
        }

        if (lOwner.distance(model.lChair3) == 0) {
            addPercept("owner", oac3);
            System.out.println("[owner] is at Chair3.");
        }

        if (lOwner.distance(model.lChair4) == 0) {
            addPercept("owner", oac4);
            System.out.println("[owner] is at Chair4.");
        }

        if (lOwner.distance(model.lSofa) == 0) {
            addPercept("owner", oasf);
            System.out.println("[owner] is at Sofa.");
        }

        if (lOwner.distance(model.lDeliver) == 0) {
            addPercept("owner", oad);
        }

        // add beer "status" the percepts
        if (model.fridgeOpen) {
            addPercept("robot", Literal.parseLiteral("stock(beer," + model.availableBeers + ")"));

        }
        if (model.medicationOpen) {

            Set<String> keys = model.medications.keySet();
            for (String key : keys) {
                addPercept("robot", Literal.parseLiteral("stock(" + key + "," + model.medications.get(key) + ")"));
            }

        }
        if (model.doseCount > 0) {
            addPercept("robot", hom);
            addPercept("owner", hom);
        }
    }

    @Override
    public boolean executeAction(String ag, Structure action) {

        System.out.println("[" + ag + "] doing: " + action);
        //java.util.List<Literal> perceptsOwner = consultPercepts("owner");
        //java.util.List<Literal> perceptsRobot = consultPercepts("enfermera");  
        //System.out.println("[owner] has the following percepts: "+perceptsOwner);
        //System.out.println("[enfermera] has the following percepts: "+perceptsRobot);

        boolean result = false;
        if (action.getFunctor().equals("sit")) {
            String l = action.getTerm(0).toString();
            Location dest = null;
            switch (l) {
                case "chair1":
                    dest = model.lChair1;
                    break;
                case "chair2":
                    dest = model.lChair2;
                    break;
                case "chair3":
                    dest = model.lChair3;
                    break;
                case "chair4":
                    dest = model.lChair4;
                    break;
                case "sofa":
                    dest = model.lSofa;
                    break;
            };
            try {
                if (ag.equals("robot")) {
                    System.out.println("[robot] is trying to sit, but it is not allowed to do it.");
                } else {
                    System.out.println("[owner] is sitting");
                    result = model.sit(1, dest);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equals(om)) { // of = open(medicalkit)
            result = model.openMedication();

        } else if (action.equals(clm)) { // clf = close(medicalkit)
            result = model.closeMedication();

        } else if (action.equals(of)) { // of = open(fridge)
            result = model.openFridge();

        } else if (action.equals(clf)) { // clf = close(fridge)
            result = model.closeFridge();

        } else if (action.getFunctor().equals("move_towards")) {
            String l = action.getTerm(0).toString();
            Location dest = null;
            if (l.equals("random_place")) {
                l = model.getRandomRest();
                System.out.println("random_place: " + l);
            }
            switch (l) {
                case "fridge":
                    dest = model.lFridge;
                    break;
                case "medicalkit":
                    dest = model.lMedication;
                    break;
                case "owner":
                    dest = model.getAgPos(1);
                    break;
                case "delivery":
                    dest = model.lDeliver;
                    break;
                case "chair1":
                    dest = model.lChair1;
                    break;
                case "chair2":
                    dest = model.lChair2;
                    break;
                case "chair3":
                    dest = model.lChair3;
                    break;
                case "chair4":
                    dest = model.lChair4;
                    break;
                case "sofa":
                    dest = model.lSofa;
                    break;
                case "washer":
                    dest = model.lWasher;
                    break;
                case "table":
                    dest = model.lTable;
                    break;
                case "doorBed1":
                    dest = model.lDoorBed1;
                    break;
                case "doorBed2":
                    dest = model.lDoorBed2;
                    break;
                case "doorBed3":
                    dest = model.lDoorBed3;
                    break;
                case "doorKit1":
                    dest = model.lDoorKit1;
                    break;
                case "doorKit2":
                    dest = model.lDoorKit2;
                    break;
                case "doorSal1":
                    dest = model.lDoorSal1;
                    break;
                case "doorSal2":
                    dest = model.lDoorSal2;
                    break;
                case "doorBath1":
                    dest = model.lDoorBath1;
                    break;
                case "doorBath2":
                    dest = model.lDoorBath2;
                    break;
            }
            try {
                if (ag.equals("robot")) {
                    result = model.moveTowards(0, dest);
                    System.out.println("robot is moving towards " + dest.toString());
                } else {
                    result = model.moveTowards(1, dest);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //owner asks to deliver all his medicines
            //} else if(action.getFunctor().equals("ask_medications")){

            //get medicine 
        } else if (action.getFunctor().equals("get") && action.getArity() == 2) {
            Term medicine = action.getTerm(0);
            String name = "";
            /* if (medicine.isStructure()) {
                Structure medication = (Structure) medicine;
                if (medication.getFunctor().equals("medicine") && medication.getArity() == 2) {
                    name = medication.getTerm(0).toString();
                    Integer amount = Integer.parseInt(medication.getTerm(1).toString());
                    Integer frec = Integer.parseInt(medication.getTerm(0).toString());
                    result = model.getMedication(name);
                }
            } else { */
            name = medicine.toString();
            //}
            System.out.println("getting medicine: " + name);
            try {
                result = model.getMedication(name, (int) ((NumberTerm) action.getTerm(1)).solve());
            } catch (Exception e) {
                logger.info("Failed to execute action get!" + e);
            }
            //hand_in medicine
        } else if (action.getFunctor().equals("hand_in") && action.getArity() == 2) {
            try {
                result = model.handInMedication((int) ((NumberTerm) action.getTerm(1)).solve());
            } catch (Exception e) {
                logger.info("Failed to execute action hand_in!" + e);
            }
            //takin medicine
        } else if (action.getFunctor().equals("sip") && action.getArity() == 1) {
            //result = model.sipMedication();
            try {
                if (ag.equals("robot")) {
                    System.out.println("[robot] is trying to take the medication, but it is not allowed to do it.");
                } else {
                    result = model.sipMedication();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (action.getFunctor().equals("deliver")) {
            // wait 4 seconds to finish "deliver"
            try {
                result = model.addMedication(action.getTerm(0).toString(), (int) ((NumberTerm) action.getTerm(1)).solve());
                Thread.sleep(4000);
            } catch (Exception e) {
                logger.info("Failed to execute action deliver!" + e);
            }

        } else {
            logger.info("Failed to execute action " + action);
        }

        if (result) {
            updatePercepts();
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
        }
        return result;
    }

    public void startCartago(String[] args) {
        cartagoEnv = new CartagoEnvironment();
        cartagoEnv.init(args);
    }

    /**
     * Called before the end of MAS execution
     */
    @Override
    public void stop() {
        super.stop();
        if (cartagoEnv != null) {
            cartagoEnv.stop();
        }
    }

}
