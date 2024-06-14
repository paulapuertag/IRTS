/* Initial beliefs*/
medication(naproxen,1,30,C).
medication(ibuprofen,1,20,C).

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

+!start : vitals(T,AVG) & (AVG>123) <- //vitals check 
   .println("ALERT critic vital signs ",AVG ," CALLING 911");
   .wait(2000);
   !start.

+!start : not medication(_,_,_,_) <-
   .print("Waiting for the medicine recipe");
   .wait(2000);
   !start.

+!start : not (medication(N,Q,P,C) & not too_soon(N)) <-
   !go_at(robot,washer);
   .print("Waiting for the medicines period");
   .wait(2000);
   !start.

+!start : medication(N,Q,P,C) & not too_soon(N)<-
   !bring(owner, medication(N,Q,P,C));    // print all members of the list
   !start.
/* getting vitals from owner */
+deleteVitals : true <- 
   .abolish(vitals(_,_)).

+addVitals(T,AVG) <-
   +vitals(T,AVG).

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
   .wait(5000);//takes 5 seconds to be charged
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
      // remember that another unit of medication has been consumed
      .date(YY, MM, DD); .time(HH, NN, SS);
      +consumed(YY, MM, DD, HH, NN, SS, N);
      !start.

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

+anotate_taken(N) : medication(N,Q,P,C)
<- .concat("I am registering that you took ", Q, " units of medication ", N, Message);
	.print(Message);
	.date(YY, MM, DD); .time(HH, NN, SS);
   +consumed(YY, MM, DD, HH, NN, SS, Medication).
