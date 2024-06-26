//package src.env;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Area;
import jason.environment.grid.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

//import jason.asSyntax.*;
/**
 * class that implements the Model of Domestic Robot application
 */
public class HouseModel extends GridWorldModel {

    // constants for the grid objects
    public static final int COLUMN = 4;
    public static final int FRIDGE = 8;
    public static final int WASHER = 16;
    public static final int SOFA = 32;
    public static final int CHAIR = 64;
    public static final int TABLE = 128;
    public static final int BED = 256;
    public static final int DOOR = 512;
    public static final int CHARGER = 1024;
    public static final int DELIVER = 2048;
    public static final int MEDICINE = 4096;

    public static final int ROBOT = 0;
    // the grid size                                                     
    public static final int GSize = 12;     //Cells
    public final int GridSize = 1080;    	//Width

    HashMap<String, Integer> medications = new HashMap<String, Integer>(); // map containing medications names next to their availability
    boolean carryingMedications = false; 	// whether the robot is carrying any medication
    String handedMedication = "";
    int doseCount = 0;	// how many dose of medication the owner did (equivalent to sip in beer)

    boolean fridgeOpen = false; 	// whether the fridge is open                                   
    boolean medicationOpen = false; 	// whether the medication cabinet is open
    String agentOpeningMedication = "";
    boolean carryingBeer = false; 	// whether the robot is carrying beer
    int sipCount = 0; 		// how many sip the owner did
    int availableBeers = 2; 		// how many beers are available

    private static final List<String> restPlaces = List.of("chair1", "chair2", "chair3", "chair4", "sofa");

    // Initialization of the objects Location on the domotic home scene 
    Location lSofa = new Location(GSize / 2, GSize - 2);
    Location lChair1 = new Location(GSize / 2 + 2, GSize - 3);
    Location lChair3 = new Location(GSize / 2 - 1, GSize - 3);
    Location lChair2 = new Location(GSize / 2 + 1, GSize - 4);
    Location lChair4 = new Location(GSize / 2, GSize - 4);
    Location lDeliver = new Location(0, 0);
    Location lWasher = new Location(GSize / 3, 0);
    Location lFridge = new Location(2, 0);
    Location lMedication = new Location(GSize * 2 - 5, GSize / 2 + 2);
    Location lCharger = new Location(GSize * 2 - 5, GSize / 2 + 4);
    Location lTable = new Location(GSize / 2, GSize - 3);
    Location lBed2 = new Location(GSize + 2, 0);
    Location lBed3 = new Location(GSize * 2 - 3, 0);
    Location lBed1 = new Location(GSize + 1, GSize * 3 / 4);

    // Initialization of the area modeling the home rooms      
    Area kitchen = new Area(0, 0, GSize / 2 + 1, GSize / 2 - 1);           		//0     ( 0, 0, 7, 5)
    Area livingroom = new Area(GSize / 3, GSize / 2 + 1, GSize, GSize - 1);			//1     ( 4, 7,12,11)
    Area bath1 = new Area(GSize / 2 + 2, 0, GSize, GSize / 3);				//2     ( 8, 0,12, 4)
    Area bath2 = new Area(GSize * 2 - 3, GSize / 2 + 1, GSize * 2 - 1, GSize - 1);	//3     (21, 7,23,11)
    Area bedroom1 = new Area(GSize + 1, GSize / 2 + 1, GSize * 2 - 4, GSize - 1);		//4     (13, 7,20,11)
    Area bedroom2 = new Area(GSize + 1, 0, GSize * 3 / 2, GSize / 3);		    	//5     (13, 0,18, 4)
    Area bedroom3 = new Area(GSize * 3 / 2 + 1, 0, GSize * 2 - 1, GSize / 3);			//6     (19, 0, 3,11)
    Area hall = new Area(0, GSize / 2 + 1, GSize / 4, GSize - 1);				//7     ( 0, 7, 3,11)
    Area hallway = new Area(GSize / 2 + 2, GSize / 2 - 1, GSize * 2 - 1, GSize / 2);	//8     ( 8, 5,23, 6) 

    public Location[][] conect = new Location[9][9];
    Map<String, Integer> rooms = new HashMap<>();

    Location lDoorHome;
    Location lDoorKit1;
    Location lDoorKit2;
    Location lDoorSal1;
    Location lDoorSal2;
    Location lDoorBed1;
    Location lDoorBed2;
    Location lDoorBed3;
    Location lDoorBath1;
    Location lDoorBath2;

    //available medications
    private void initMedications() {
        medications.put("naproxen", 20);
        medications.put("adderall", 2);
        medications.put("omeprazol", 2);
        medications.put("omoxicillin", 2);
        medications.put("ibuprofen", 10);
    }

    private void initRooms() {

        rooms.put("kitchen", 0);
        rooms.put("livingroom", 1);
        rooms.put("bath1", 2);
        rooms.put("bath2", 3);
        rooms.put("bedroom1", 4);
        rooms.put("bedroom2", 5);
        rooms.put("bedroom3", 6);
        rooms.put("hall", 7);
        rooms.put("hallway", 8);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                conect[i][j] = new Location(-1, -1);
            }
        }

        // Initialization of the doors location on the domotic home scene 
        lDoorHome = new Location(0, GSize - 1);
        lDoorKit1 = new Location(0, GSize / 2);
        conect[0][7] = lDoorKit1;
        conect[7][0] = lDoorKit1;
        lDoorKit2 = new Location(GSize / 2 + 1, GSize / 2 - 1);
        conect[0][8] = lDoorKit2;
        conect[8][0] = lDoorKit2;
        lDoorSal1 = new Location(GSize / 4, GSize - 1);
        conect[1][7] = lDoorSal1;
        conect[7][1] = lDoorSal1;
        lDoorSal2 = new Location(GSize / 2 + 2, GSize / 2);
        conect[1][8] = lDoorSal2;
        conect[8][1] = lDoorSal2;
        lDoorBed1 = new Location(GSize + 1, GSize / 2);
        conect[4][8] = lDoorBed1;
        conect[8][4] = lDoorBed1;
        lDoorBath1 = new Location(GSize - 1, GSize / 4 + 1);
        conect[1][2] = lDoorBath1;
        conect[2][1] = lDoorBath1;
        lDoorBed3 = new Location(GSize * 2 - 1, GSize / 4 + 1);
        conect[6][8] = lDoorBed3;
        conect[8][6] = lDoorBed3;
        lDoorBed2 = new Location(GSize + 1, GSize / 4 + 1);
        conect[5][8] = lDoorBed2;
        conect[8][5] = lDoorBed2;
        lDoorBath2 = new Location(GSize * 2 - 4, GSize / 2 + 1);
        conect[4][3] = lDoorBath2;
        conect[3][4] = lDoorBath2;
    }

    /*
	Modificar el modelo para que la casa sea un conjunto de habitaciones
	Dar un codigo a cada habitación y vincular un Area a cada habitación
	Identificar los objetos de manera local a la habitación en que estén
	Crear un método para la identificación del tipo de agente existente
	Identificar objetos globales que precisen de un único identificador
     */
    public HouseModel() {
        // create a GSize x 2GSize grid with 3 mobile agent
        super(2 * GSize, GSize, 2);

        // initial location of robot (column 3, line 3)
        // ag code 0 means the robot
        setAgPos(ROBOT, 2, 4);
        setAgPos(1, GSize / 2 + 2, GSize - 3);
        //setAgPos(2, GSize*2-1, GSize*3/5); 

        initRooms();
        initMedications();

        // Do a new method to create literals for each object placed on
        // the model indicating their nature to inform agents their existence
        // initial location of fridge and owner
        add(FRIDGE, lFridge);
        add(MEDICINE, lMedication);
        add(CHARGER, lCharger);
        add(WASHER, lWasher);
        add(DELIVER, lDeliver);
        add(SOFA, lSofa);
        add(CHAIR, lChair2);
        add(CHAIR, lChair3);
        add(CHAIR, lChair4);
        add(CHAIR, lChair1);
        add(TABLE, lTable);
        add(BED, lBed1);
        add(BED, lBed2);
        add(BED, lBed3);

        add(DOOR, lDoorHome);
        addWall(GSize / 2 + 1, 0, GSize / 2 + 1, GSize / 4 + 1); 		//	 (7,0,7,4)
        add(DOOR, lDoorKit2);
        //addWall(GSize/2+1, GSize/2-1, GSize/2+1, GSize-2);  
        add(DOOR, lDoorSal1);

        addWall(GSize / 2 + 1, GSize / 4 + 1, GSize - 2, GSize / 4 + 1);  //	 (7,4,10,4) 
        //addWall(GSize+1, GSize/4+1, GSize*2-1, GSize/4+1);   
        add(DOOR, lDoorBath1);
        //addWall(GSize+1, GSize*2/5+1, GSize*2-2, GSize*2/5+1);   
        addWall(GSize + 2, GSize / 4 + 1, GSize * 2 - 2, GSize / 4 + 1);  //	 (14,4,22,4) 
        addWall(GSize * 2 - 6, 0, GSize * 2 - 6, GSize / 4);          //	 (18,0,18,3)
        add(DOOR, lDoorBed1);      // Location(GSize+1, GSize/2);    

        addWall(GSize, 0, GSize, GSize / 4 + 1);                //	 (12,0,12,4)
        //addWall(GSize+1, GSize/4+1, GSize, GSize/4+1);  
        add(DOOR, lDoorBed2);

        addWall(1, GSize / 2, GSize / 2 + 1, GSize / 2);            //	 (1,6,7,6)
        add(DOOR, lDoorKit1); //Location(GSize/2+1, GSize/2-1);                
        add(DOOR, lDoorSal2); //Location(GSize/2+2, GSize/2);

        addWall(GSize / 4, GSize / 2 + 1, GSize / 4, GSize - 2);      //	 (3,7,3,10)      

        addWall(GSize, GSize / 2, GSize, GSize - 1);            //	 (12,6,12,11)
        addWall(GSize * 2 - 4, GSize / 2 + 2, GSize * 2 - 4, GSize - 1);  //	 (20,8,20,11)
        addWall(GSize / 2 + 3, GSize / 2, GSize, GSize / 2);        //	 (9,6,12,6)
        addWall(GSize + 2, GSize / 2, GSize * 2 - 1, GSize / 2);      //	 (14,6,23,6)
        add(DOOR, lDoorBed3);
        add(DOOR, lDoorBath2);

    }

    private Integer getMedicationAvailable(String medication) {
        if (medications.get(medication) != null) {
            return medications.get(medication);
        } else {
            return -1;
        }
    }

    String getRoom(Location thing) {

        String byDefault = "kitchen";

        if (bath1.contains(thing)) {
            byDefault = "bath1";
        };
        if (bath2.contains(thing)) {
            byDefault = "bath2";
        };
        if (bedroom1.contains(thing)) {
            byDefault = "bedroom1";
        };
        if (bedroom2.contains(thing)) {
            byDefault = "bedroom2";
        };
        if (bedroom3.contains(thing)) {
            byDefault = "bedroom3";
        };
        if (hallway.contains(thing)) {
            byDefault = "hallway";
        };
        if (livingroom.contains(thing)) {
            byDefault = "livingroom";
        };
        if (hall.contains(thing)) {
            byDefault = "hall";
        };
        return byDefault;
    }

    boolean sit(int Ag, Location dest) {
        Location loc = getAgPos(Ag);
        if (loc.isNeigbour(dest) && Ag != ROBOT) {
            setAgPos(Ag, dest);
        };
        return true;
    }

    boolean openMedication(String ag) {
        if (!medicationOpen) {
            medicationOpen = true;
            agentOpeningMedication = ag;
            return true;
        } else {
            return false;
        }
    }

    boolean closeMedication() {
        if (medicationOpen) {
            medicationOpen = false;
            agentOpeningMedication = "";
            return true;
        } else {
            return false;
        }
    }

    boolean openFridge() {
        if (!fridgeOpen) {
            fridgeOpen = true;
            return true;
        } else {
            return false;
        }
    }

    boolean closeFridge() {
        if (fridgeOpen) {
            fridgeOpen = false;
            return true;
        } else {
            return false;
        }
    }

    boolean canMoveTo(int Ag, int x, int y) {
        if (Ag < 1) {
            return (isFree(x, y) && !hasObject(WASHER, x, y) && !hasObject(TABLE, x, y)
                    && !hasObject(SOFA, x, y) && !hasObject(CHAIR, x, y));
        } else {
            return (isFree(x, y) && !hasObject(WASHER, x, y) && !hasObject(TABLE, x, y));
        }
    }

    boolean moveTowards(int Ag, Location dest) {
        Location r1 = getAgPos(Ag);
        Location r2 = getAgPos(Ag);

        String agRoom = getRoom(r1);
        String destRoom = getRoom(dest);

        if (agRoom.equals(destRoom)) {
            System.out.println("Agent is on the right room.......................");
            System.out.println("Room: " + destRoom);
            System.out.println("The room code is: " + rooms.get(destRoom));
        } else {
            int from = rooms.get(agRoom);
            int to = rooms.get(destRoom);
            int dist = r1.distance(dest);

            System.out.println("The agent on " + agRoom + " must go towards room: " + destRoom);
            Location noDoor = new Location(-1, -1);
            Location door = conect[from][to];
            Location ddest = door;

            if (!(door.equals(noDoor))) {
                System.out.println("The agent could use the door at: " + door);
            } else {
                for (int i = 0; i < 9; i++) {
                    ddest = conect[from][i];
                    if (!(ddest.equals(noDoor))) {
                        if (r1.distance(ddest) < dist) {
                            door = ddest;
                            System.out.println("The agent must use the door at: " + door);
                        }
                    }
                };
                System.out.println("The nearest door is at: " + door);
            };
            //moveTowards(Ag,door);
            if (!(r1.equals(door))) {
                dest = door;
            }
        };

        if (r1.distance(dest) > 0) {

            if (r1.x < dest.x && canMoveTo(Ag, r1.x + 1, r1.y)) {
                r1.x++;
            } else if (r1.x > dest.x && canMoveTo(Ag, r1.x - 1, r1.y)) {
                r1.x--;
            } else if (r1.y < dest.y && canMoveTo(Ag, r1.x, r1.y + 1)) {
                r1.y++;
            } else if (r1.y > dest.y && canMoveTo(Ag, r1.x, r1.y - 1)) {
                r1.y--;
            };

        };
        if (r1.equals(r2) && r1.distance(dest) > 0) { // could not move the agent
            if (r1.x == dest.x && canMoveTo(Ag, r1.x + 1, r1.y)) {
                r1.x++;
            } else if (r1.x == dest.x && canMoveTo(Ag, r1.x - 1, r1.y)) {
                r1.x--;
            } else if (r1.y == dest.y && canMoveTo(Ag, r1.x, r1.y - 1)) {
                r1.y--;
            } else if (r1.y == dest.y && canMoveTo(Ag, r1.x, r1.y + 1)) {
                r1.y++;
            }
            System.out.println("update agent: " + Ag + "to: " + r1);
        };

        setAgPos(Ag, r1); // update the agent's location in the grid 

        return true;
    }

    boolean getMedication(String medication, Integer requiredQuantity, String agent) {
        Integer availability = getMedicationAvailable(medication);
        if (availability != -1) {
            if (medicationOpen && availability >= requiredQuantity && !carryingMedications) {
                medications.put(medication, availability - requiredQuantity);
                carryingMedications = true;
                System.out.println("Taking " + requiredQuantity + " unit(s) of " + medication + ". Current avalability: " + getMedicationAvailable(medication));
                return true;
            } else {
                if (medicationOpen) {
                    System.out.println("The medicine cabinet is opened. ");
                };
                if (availability > requiredQuantity) {
                    System.out.println("Medicine cabinet has enough of that medication. ");
                } else if (availability < requiredQuantity) {
                    System.out.println("Medicine cabinet dont has enough of that medication. ");
                };
                if (!carryingMedications && agent.equals("robot")) {
                    System.out.println("The robot is not bringing the medication. ");
                };
                return false;
            }
        } else {
            System.out.println("There's no medication with name '" + medication + "' available in the cabinet.");
            return false;
        }
    }

    boolean addMedication(String medication, Integer n) {
        int medicationInitialQuantity = getMedicationAvailable(medication);
        if (medicationInitialQuantity != -1) {
            medications.put(medication, medicationInitialQuantity + n);
            System.out.println("The robot increment the amount of " + medication + ". Actual stock: " + medications.get(medication));
            return true;
        } else {
            System.out.println("There's no medication with name '" + medication + "' available in the cabinet.");
            return true;
        }
    }

    boolean handInMedication(String medication, Integer dose, String agent) {
        if (carryingMedications || agent.equals("owner")) {
            handedMedication = medication;
            doseCount = dose;
            carryingMedications = false;
            //if (view != null)
            //view.update(lOwner.x,lOwner.y);
            return true;
        } else {
            return false;
        }
    }

    boolean sipMedication() {
        if (doseCount > 0) {
            doseCount--;
            System.out.println("Owner is having a dose of medication " + handedMedication);
            //if (view != null)
            //view.update(lOwner.x,lOwner.y);
            return true;
        } else {
            return false;
        }
    }

    public String getRandomRest() {
        Random rand = new Random();
        return restPlaces.get(rand.nextInt(restPlaces.size()));
    }
}
