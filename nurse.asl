/* Initial beliefs and rules */

// initially, I believe that there is some medication in the fridge
available(ibuprofen, medication).

// my owner should not consume more than the limit a day 
// TODO: should be informed by the owner at the beginning
limit(ibuprofen, 5, 8). // the owner should take 5 units of ibuprofen every 8 hours

// TODO: modificar para que tambien tome en cuenta la ultima vez que se tomo la medicina
too_much(M) :- // M is variable, if a name starts with capital letter, it's considered a variable
   .date(YY,MM,DD) &	 // can be used: &,|,not,~(extrict not)
   .time(HH,MIN,SS) &
   .count(consumed(YY,MM,DD,HH,_,_,M),QtdM) &
   limit(M,Limit,_) &
   QtdM > Limit.
   
too_soon(M) :- 
   .date(YY,MM,DD) &	
   .time(HH,MIN,SS) &
   consumed(YY,MM,DD,TakenHour,_,_,M) &
   limit(M,Limit,F) &
   TakenHour > (HH-F).

// Prolog-like rule
owner_liar(Qtd,Qi,Qf) :-
	Qf > Qi-Qtd.


/* Plans */

+!bring(owner, M)
 :  available(M, medication) & not too_much(M) //& not too_soon(M) //green shows a believe
   <- !go_at(robot, medication);
      open(medication);	// orange shows that something is requested to be changed in environment
      get(M);
      close(medication);
      !go_at(robot, owner);
      hand_in(M);
      ?has(owner, M);
      // remember that another unit of medication has been consumed
      .date(YY, MM, DD); .time(HH, NN, SS);
      +consumed(YY, MM, DD, HH, NN, SS, M).
      //.wait(F*3600); // wait for F hours(seconds) before bringing the medication again
      //+!bring(owner, M).

+!bring(owner,M) : not available(M,medication) <- 
	!movingHome(robot);
	!update(M);   
	.wait(300);
	!bring(owner, M).
/*
+!bring(owner, M) :  not available(M, fridge)
   <- !acquire_medication(M);
      !go_at(robot,delivery). // go to delivery and wait there.
*/

+!bring(owner, M) : (too_much(M) | too_soon(M)) & limit(M,Q,F) // (Think is done) Change the formula to adjust with periodicity
   <- .concat("The Department of Health does not allow me to give you more than ", Q,
              " units of ", M, " every " , F , " hours! I am very sorry about that!", Msg);
	!go_at(robot,washer);//!go_at(robot, sofa);
   .send(owner, tell, msg(Msg));
	.wait(30000);
	.abolish(consumed(_,_,_,_,_,_,_));     
	.send(owner,tell,msg("It is a new day, you could take drugs again."));  
	.println("Is is a new day, owner could take drugs again."); 
   !bring(owner, M).

-!bring(_, _)
   :  true // Adapt it accordingly with previously updated predicates
   <- .current_intention(I);
      .print("Failed to achieve goal '!bring(_,_)'. Current intention is: ",I).

+!movingHome(robot) <-
	!go_at(robot,fridge); 
   !waitRandom;                                           
	!go_at(robot,medication);
	!waitRandom;
	!go_at(robot,delivery);
	!waitRandom.  
	
+!waitRandom <-
	.random(X); 
	.wait(X*500+200).       
	       
+!update(M) <-
	deliver(M,3);
	+available(M,medication);
	!go_at(robot,medication).
	//.abolish(stock(Something,_));
	//+stock(Something,3).
/*
+!acquire_medication(M) : true
   <- +delivered(M, 3, _).
	//+stock(M,3).// estaría bien si solo quisieramos
	//llenar más medicina en la nevera, pero estaríamos 
	//olvidando la parte de llevársela al owner
*/

+!go_at(robot,P) : at(robot,P) <- true.
+!go_at(robot,P) : not at(robot,P)
  <- move_towards(P);
     !go_at(robot,P).

// when the supermarket makes a delivery, try the 'has' goal again
+delivered(M, _Qtd, _OrderId)[source(supermarket)]
  :  true
  <- !go_at(robot, delivery);
  	.wait(200);
	!go_at(robot, fridge);
	.wait(200);
	+available(M, fridge);
	!bring(owner, M).

// when the medication is opened, the medication stock is perceived
// and thus the available belief is updated
+stock(M, 0) :  available(M, medication)
   <- -available(M, medication).
+stock(M, N) :  N > 0 & not available(M, medication) & at(robot,medication)
   <- -+available(M, medication). // generates again available event
//*
+stock(M,N) :  N > 0 & not available(M,medication) & not at(robot,medication)
   <- !go_at(robot,medication).
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
