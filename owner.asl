/* owner */
/* Initial goals */

!start. 
!setupTool("Owner", "Robot"). 

// Beliefs about medication
medication(ibuprofen, 1, 4). // the owner should take 1 units of ibuprofen every 4 hours

// initial goal: get medication
// initial goal: get medication
+!start <-
   //{!check_bored}.
	{  !get(Medication) ||| !check_bored}.//; !start. 
	    
+!setupTool(Name, Id)
	<- 	makeArtifact("GUI","gui.Console",[],GUI);
		setBotMasterName(Name);
		setBotName(Id);
		focus(GUI). 

// here Medication is the three param fact medication(ibuprofen, 1, 4)
+!get(Medication) : true
   <- .send(robot, achieve, bring(owner,Medication)).

+has(owner,Medication) : true
   <- !take(Medication).
-has(owner,Medication) : true
   <- !start.//!get(medication).

// if I have not medication finish, in other case while I have medication, take
+!take(Medication) : not has(owner,Medication) // we change 'drink(beer)' for 'take(medication)'
   <- true.
+!take(medication(M, Q, _)) : has(owner,medication(_, _, _)) & medication(M, Q, _)
   <- taking(medication(M, Q, _)); // we change 'sip(beer);' for 'take(M,Q)'
      //.wait(F*3600*1000); // wait for F hours before taking the medication again
      !take(medication(M, Q, _)).

+!check_bored : true
   <- .random(X); .wait(X*2000 + 10000);// i get bored at random times
      !go_at(owner, random_place);
      //.send(robot, askOne, time(_), R); // when bored, I ask the robot about the time
      .print(R);
      !start.//!check_bored.

+!go_at(owner,P) : at(owner,P) <- true.
+!go_at(owner,P) : not at(owner,P)
  <- move_towards(P);
     !go_at(owner,P).

+msg(M)[source(Ag)] : true
   <- .print("Message from ",Ag,": ",M);
      -msg(M).
