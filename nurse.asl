/* Initial beliefs and rules */

// initially, I believe that there is some medication in the medical kit
available(ibuprofen, medicalkit).

// my owner should not consume more than the limit a day 
// TODO: should be informed by the owner at the beginning
limit(ibuprofen, 1, 4). // the owner should not take more than 5 units of ibuprofen every 8 hours
limit(naproxen, 1, 8).

// TODO: modificar para que tambien tome en cuenta la ultima vez que se tomo la medicina
too_much(M) :- // M is variable, if a name starts with capital letter, it's considered a variable
   .date(YY,MM,DD) &	 // can be used: &,|,not,~(extrict not)
   .time(HH,MIN,SS) &
   .count(consumed(YY,MM,DD,HH,_,_,M),QtdM) &
   limit(M,Limit,_) &
   QtdM >= Limit.
   
too_soon(M) :- 
   .date(YY,MM,DD) &	
   .time(HH,MIN,SS) &
   consumed(YY,MM,DD,TakenHour,_,_,M) &
   limit(M,Limit,F) &
   TakenHour >= (HH-F).

// Prolog-like rule
owner_liar(Qtd,Qi,Qf) :-
	Qf > Qi-Qtd.


/* Plans */

+!bring(owner, medication(M,Q,F))
 :  available(M, medicalkit) & not too_much(M) //& not too_soon(M) //green shows a believe
   <- .println("BRINGING OWNER MEDICATION ",M); 
      !go_at(robot, medicalkit);
      open(medicalkit);	// orange shows that something is requested to be changed in environment
      get(M);
      close(medicalkit);
      !go_at(robot, owner);
      hand_in(M);
      ?has(owner, M);
      // remember that another unit of medication has been consumed
      .date(YY, MM, DD); .time(HH, NN, SS);
      +consumed(YY, MM, DD, HH, NN, SS, M).
      //.wait(F*3600); // wait for F hours(seconds) before bringing the medication again
      //+!bring(owner, M).

+!bring(owner,medication(M,Q,F)) : not available(M,medicalkit) <- 
   .println("GETTING MORE MEDICATION ",M); 
	!movingHome(robot);
	!update(M);   
	.wait(300);
	!bring(owner, medication(M,Q,F)).

//comento esto para probar solo con limite de cantidad
//+!bring(owner, M) : (too_much(M) | too_soon(M)) & limit(M,Q,F) 
+!bring(owner, medication(M,Q,F)) : too_much(M) & limit(M,Q,F) // (Think is done) Change the formula to adjust with periodicity
   <-
   .println("STOP TO much medication ",M);
   .concat("The Department of Health does not allow me to give you more than ", Q,
              " units of ", M, " every " , F , " hours! I am very sorry about that!", Msg);
	!go_at(robot,washer);//!go_at(robot, sofa);
   .send(owner, tell, msg(Msg));
	.wait(30000);
	.abolish(consumed(_,_,_,_,_,_,_));     
	.send(owner,tell,msg("It is a new day, you could take drugs again."));  
	.println("Is is a new day, owner could take drugs again."); 
   !bring(owner, medication(M,Q,F)).

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
	//.abolish(stock(Something,_));
	//+stock(Something,3).

+!go_at(robot,P) : at(robot,P) <- true.
+!go_at(robot,P) : not at(robot,P)
  <- move_towards(P);
     !go_at(robot,P).

// when the supermarket makes a delivery, try the 'has' goal again
+delivered(M, _Qtd, _OrderId)[source(supermarket)]
  :  true
  <- !go_at(robot, delivery);
  	.wait(200);
	!go_at(robot, medicalkit);//medication here is the medical kit
	.wait(200);
	+available(M, medicalkit);
	!bring(owner, M).

// when the medication is opened, the medication stock is perceived
// and thus the available belief is updated
+stock(M, 0) :  available(M, medicalkit)
   <- -available(M, medicalkit).
+stock(M, N) :  N > 0 & not available(M, medicalkit) & at(robot,medicalkit)
   <- -+available(M, medicalkit). // generates again available event
//*
+stock(M,N) :  N > 0 & not available(M,medicalkit) & not at(robot,medicalkit)
   <- !go_at(robot,medicalkit).
//*/ 
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
