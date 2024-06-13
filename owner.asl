/* owner */
/* Initial goals */
medication(naproxen,1,2,C).
medication(ibuprofen,1,4,C).

//!start. 
!setupTool("Owner", "Robot"). 
	    
+!setupTool(Name, Id)
	<- makeArtifact("GUI","gui.Console",[] , GUI);
		setBotMasterName(Name);
		setBotName(Id);
		focus(GUI). 

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


+has(owner,M) : true
   <- !take(M).

-has(owner,M) : true
   <- true.//!start.//!get(medication).

// if the owner does not have medication finish, in other case while I have medication, take
+!take(M) : not has(owner,M) // we change 'drink(beer)' for 'take(medication)'
   <- true.
   
+!take(M) : has(owner,M) //& medication(M, Q, F)
   <- sip(M);
      !take(M).

+!check_bored : true
   <- .random(X); 
      .print("this is owners random wait ", X*2000 + 10000);
      .wait((X*2000 + 10000)*10);// i get bored at random times
      !go_at(owner, random_place);
      //.send(robot, askOne, time(_), R); // when bored, I ask the robot about the time
      .print(R);
      !start.
	  
+!warn_taken(Medication,Qtd) : medication(Medication, _, _)
   <- .send(robot, tell, check_taken(Medication,Qtd)).

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