/* Initial beliefs*/
medication(naproxen,1,3,C).
medication(ibuprofen,1,1,C).

// initially, I believe that there is some medication in the medical kit

battery(100).
critical_battery(5).

/* Goals */ 
!check_inventary.

+!check_inventary <-
   !go_at(robot,medicalkit);
   open(medicalkit);
   .wait(300);
   close(medicalkit);
   !start.

+!start : not medication(_,_,_,_) <-
   .print("Waiting for the medicine recipe");
   .wait(2000);
   !start.

+!start : not (medication(N,Q,P,C) & not too_soon(N)) <-
   .print("Waiting for the medicines period");
   .wait(2000);
   !start.

+!start : medication(N,Q,P,C) & not too_soon(N)<-
   !bring(owner, medication(N,Q,P,C));    // print all members of the list
   !start.

/*Medicine fact Updates from owner*/

+deleteMedicineFacts : true <- 
   .abolish(medication(_,_,_,_));
   .abolish(minutes_to_serve(_,_)).

+addMedication(N,Q,P,C) : not medication(N,Q,P,C) <-
   .count(medication(_,_,_,_),Nmed);
   +medication(N,Q,P,C).

/* Rules */
too_much(N) :- // N is variable, if a name starts with capital letter, it's considered a variable
   .date(YY,MM,DD) &	 // can be used: &,|,not,~(extrict not)
   .time(HH,MIN,SS) &
   .count(consumed(YY,MM,DD,HH,_,_,N),QtdM) &
   medication(N,Q,P,C) &
   QtdM >= Q.
   
too_soon(N) :- 
   .date(YY,MM,DD) &	
   .time(HH,MIN,SS) &
   consumed(YY,MM,DD,TakenHour,TakenMin,_,N) & 
   medication(N,Q,P,C) &
   TakenMin+P > MIN.//TakenHour+P > HH.
   
// Prolog-like rule
owner_liar(Qtd,Qi,Qf) :-
	Qf > Qi-Qtd.

//** batery logic & charger logic**//
low_battery :- 
   battery(B) & critical_battery(C) & B<=C.

+!use_battery : battery(B) & not low_battery <-
   -+battery(B-1).

+!use_battery : battery(B) & low_battery <-
   +going_to_charge;
   !go_charge.

+!go_charge : low_battery <-
   .print("Need to recharge my battery");
   !go_at(robot, charger);
   .print("Charging...");
   .wait(2*1000);//takes 2 minutes to be charged
   -going_to_charge;
   -+battery(100).

+battery(B) : true <-
   .print("My battery is ", B,"%").

/*nurse functionality */
+!bring(owner, medication(N,Q,P,C))
 :  available(N, medicalkit) & not too_soon(N) 
   <- .println("BRINGING OWNER ",Q," UNITS OF MEDICATION ",N); 
      !go_at(robot, medicalkit);
      open(medicalkit);	// orange shows that something is requested to be changed in environment
      get(N,Q);
      close(medicalkit);
      !go_at(robot, owner);
      hand_in(N,Q);
      ?has(owner, N);
      // remember that another unit of medication has been consumed
      .date(YY, MM, DD); .time(HH, NN, SS);
      +consumed(YY, MM, DD, HH, NN, SS, N);
      !start.
      //!bring(owner, medication(N,Q,P,C)).

+!bring(owner,medication(N,Q,P,C)) : not available(N,medicalkit) <- 
   .println("GETTING MORE MEDICATION ",N); 
	!movingHome(robot);
	!update(N);   
	.wait(300);
	!bring(owner, medication(N,Q,P,C)).

//comento esto para probar solo con limite de cantidad
+!bring(owner, medication(N,Q,P,C)) 
   : too_soon(N) & medication(N,Q,P,C) // (Think is done) Change the formula to adjust with periodicity
   <-
   .concat("The Department of Health does not allow me to give you more than ", Q,
              " units of ", N, " every " , P , " hours! I am very sorry about that!", Msg);
	!go_at(robot,washer);
   .send(owner, tell, msg(Msg));
   //!bring(owner, medication(N,Q,P,C)).
   !start.

-!bring(_, _)
   :  true // Adapt it accordingly with previously updated predicates
   <- .current_intention(I);
      .print("Failed to achieve goal '!bring(_,_)'. Current intention is: ",I).

+!movingHome(robot) <-                                          
	!go_at(robot,medicalkit);
	!waitRandom;
	!go_at(robot,delivery);
	!waitRandom.  
	
+!waitRandom <-
	.random(X); 
	.wait(X*500+200).       
	       
+!update(M) <-
	deliver(M,3);
	+available(M,medicalkit);
	!go_at(robot,medicalkit).


//** movement **//
+!go_at(robot,P) : at(robot,P) <- true.

+!go_at(robot,P) : not at(robot,P) & going_to_charge
  <- move_towards(P);
     !go_at(robot,P).

+!go_at(robot,P) : not at(robot,P) & not going_to_charge
  <- move_towards(P);
     !use_battery;
     !go_at(robot,P).

// when the supermarket makes a delivery, try the 'has' goal again
/*
+delivered(M, _Qtd, _OrderId)[source(supermarket)]
  : true <- 
   !go_at(robot, delivery);
  	.wait(200);
	!go_at(robot, medicalkit);//medication here is the medical kit
	.wait(200);
	+available(M, medicalkit);
	!bring(owner, M).
*/

// when the medication is opened, the medication stock is perceived
// and thus the available belief is updated
+stock(M, 0) :  available(M, medicalkit)
   <- -available(M, medicalkit).
+stock(M, N) :  N > 0 & not available(M, medicalkit) & at(robot,medicalkit)
   <- +available(M, medicalkit). // generates again available event
+stock(M,N) :  N > 0 & not available(M,medicalkit) & not at(robot,medicalkit)
   <- !go_at(robot,medicalkit).

+?time(T) : true
  <-  time.check(T). // Internal action implemented in JAVA => Folder.class(Params)

+check_taken(Medication,Qtd)[source(owner)] : true
<- !go_at(robot, medication);
	?stock(Medication,Qi);
	open(medication);
   close(medication);
	?stock(Medication,Qf);
	!inform_owner(Medication,Qtd,Qi,Qf).
	
+!inform_owner(Medication,Qtd,Qi,Qf) : not owner_liar(Qtd,Qi,Qf)
<- 	.concat("I am registering that you took ", Qtd, " units of medication ", Medication, Msg);
	.send(owner,tell,msg(Msg));
	.date(YY, MM, DD); .time(HH, NN, SS);
     +consumed(YY, MM, DD, HH, NN, SS, Medication).
	 
+!inform_owner(Medication,Qtd,Qi,Qf) : owner_liar(Qtd,Qi,Qf)
<- 	.concat("You are lying me, you didn't take your dose", Msg);
	.send(owner,tell,msg(Msg)).