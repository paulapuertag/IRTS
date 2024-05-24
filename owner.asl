/* owner */
/* Initial goals */

//!start. 
!setupTool("Owner", "Robot"). 

// Beliefs about medication
//medication(ibuprofen, 1, 4). // the owner should take 1 units of ibuprofen every 4 hours
//medication(naproxen, 1, 8).

// initial goal: get medication
//+!start <-
//   !check_bored.
	    
+!setupTool(Name, Id)
	<- 	makeArtifact("GUI","gui.Console",[],GUI);
		setBotMasterName(Name);
		setBotName(Id);
		focus(GUI). 

// here Medication is the three param fact medication(ibuprofen, 1, 4)
+!get(Medication) : true
   <- .send(robot, achieve, bring(owner,Medication)).

+!ask_medications : medication(M, Q, F)
   <- .send(robot, achieve, bring(owner,medication(M, Q, F))).

+has(owner,M) : true
   <- !take(M).
-has(owner,M) : true
   <- true.//!start.//!get(medication).

// if I have not medication finish, in other case while I have medication, take
+!take(M) : not has(owner,M) // we change 'drink(beer)' for 'take(medication)'
   <- true.
   
+!take(M) : has(owner,M) //& medication(M, Q, F)
   <- sip(M); // we change 'sip(beer);' for 'taking(M)'
      //.wait(F*3600*1000); // wait for F hours before taking the medication again
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
