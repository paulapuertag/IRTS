/* owner */
/* Initial goals */
medication(naproxen,1,30,C).
medication(ibuprofen,1,20,C).

!setupTool("Owner", "Robot"). 

+!setupTool(Name, Id)
	<- makeArtifact("GUI","gui.Console",[] , GUI);
		setBotMasterName(Name);
		setBotName(Id);
		focus(GUI). 

/** vitals functionality **/
//!check_vitals.

+?time(T) : true
  <-  time.check(T). // Internal action implemented in JAVA => Folder.class(Params)

+!check_vitals : true <-
   getVitals(Name);
   .println("current vital: ", vitals(T,AVG));   
   .at("now +1 s", {+!check_vitals}).

//receive a vital (signal) from gui console.java
+updateVitals(T,AVG) <-
      .abolish(vitals(_,_));
      .broadcast(tell, deleteVitals);
      .println("Updating vitals: ", vitals(T,AVG));
      .send(robot, tell, addVitals(T,AVG));
      +vitals(T,AVG).

//receive a new medicine (signal) from gui console.java
+medUpdate(N) <-
      .abolish(medication(_,_,_,_));
      .broadcast(tell, deleteMedicineFacts);
      if (not medication(N,Q,P,C)){
         .println("I have deleted the medicines");
      }else{
         .println(" Medicine (remains):", medication(N,Q,P,C));
      }.

+newMedication(N,Q,P,C): not medication(N,Q,P,C) <- 
   .println("I have added the medicine: ", medication(N,Q,P,C));
   .send(robot, tell, addMedication(N,Q,P,C));
   +medication(N,Q,P,C).


+has(owner,M)
   <- !take(M).

-has(owner,M)
   <- true.

// if the owner does not have medication finish, in other case while I have medication, take
+!take(M) : not has(owner,M) // we change 'drink(beer)' for 'take(medication)'
   <- true.
   
+!take(M) : has(owner,M) 
   <- sip(M);
      !take(M).

+!check_bored : true
   <- .random(X); 
      .print("this is owners random wait ", X*2000 + 10000);
      .wait((X*2000 + 10000)*10);// i get bored at random times
      !go_at(owner, random_place);
      .print(R);
      !start.

//take_independently called in Console.java with a signal
+take_independently(N) : medication(N,Q,P,C) <-
   !go_at(owner, medicalkit);
   open(medicalkit);
   get(N,Q);
   close(medicalkit);
   hand_in(N,Q);
   .concat("I have taken the prescribed dose of medication ",N, Message);
   .print(Message);
   !go_at(owner, chair1);
   .send(robot, tell, anotate_taken(N)).

+take_independently(N) : not medication(N,Q,P,C) <-
   show("No such medication in the cabinet").

+!go_at(owner,P) : at(owner,P) <- true.
+!go_at(owner,P) : not at(owner,P)
  <- move_towards(P);
     !go_at(owner,P).

+msg(M)[source(Ag)] : true
   <- .print("Message from ",Ag,": ",M);
      -msg(M).

+say(M) : .concat("I receive the message ", M, Message) <- 
      +newMessage(Something)[source(percept)];
      .wait(100);
      show(Message).
