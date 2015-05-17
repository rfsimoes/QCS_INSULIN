

active proctype voter(){

	int vec[3];
	int hashmapKey[9];
	int hashmapVal[9];
	int i,j,k,countKey,countVal,found,counter,found2;
	int majorCount,majorResult,maiority;

	countKey=0;
	countVal=0;
	counter=0;
	i=0;
	j=0;
	k=0;
	countKey=0;
	countVal=0;
	found=0;
	counter=0;
	found2=0;
	majorCount=0;
	majorResult=0;
	maiority=0;

	vec[0]=0;
	vec[1]=0;
	vec[2]=0;

	hashmapKey[0]=0;
	hashmapKey[1]=0;
	hashmapKey[2]=0;
	hashmapKey[3]=0;
	hashmapKey[4]=0;
	hashmapKey[5]=0;
	hashmapKey[6]=0;
	hashmapKey[7]=0;
	hashmapKey[8]=0;

	hashmapVal[0]=0;
	hashmapVal[1]=0;
	hashmapVal[2]=0;
	hashmapVal[3]=0;
	hashmapVal[4]=0;
	hashmapVal[5]=0;
	hashmapVal[6]=0;
	hashmapVal[7]=0;
	hashmapVal[8]=0;


	for(i : 0..2){
		select(j : 1..51);
		counter++;
		vec[i] = j
	}
	/*vec[0]=1;
	vec[1]=1;
	vec[2]=3;
	counter=3;*/
	printf("vec = %d %d %d counter = %d\n",vec[0],vec[1],vec[2],counter);
	
	if
	:: counter<=1->printf("-2\n")
	:: else->printf("counter>1\n");skip
	fi;	

	for(i : 0..2){
		found=0;
		//printf("Resultado: %d\n", vec[i]);
		for(j:0..8){
			//printf("ciclo 0..8 -> %d\n",j);
			//printf("compare %d %d\n",hashmapKey[j] , vec[i]);
			if
			:: found==1->
			//printf("found=1\n");
			skip
			:: hashmapKey[j] == vec[i] -> //printf("encontra\n");
			found2=0;
			for(k:0..8){
				if
				:: found2==1->skip
				:: hashmapKey[k] == vec[i]-1 -> 
				hashmapVal[k]=hashmapVal[k]+10000;
				//printf("vel i-1 %d hashmapKey %d\n",vec[i]-1,hashmapKey[k]);
				found2=1
				:: timeout->skip
				fi
			}
			if
			:: found2==0->
			//printf("vel i-1 nao encontrado\n");
			hashmapKey[countKey]=vec[i]-1;
			countKey++;
			hashmapVal[countVal]=10000;
			countVal++;
			:: else->skip
			fi;

			//hashmapVal[j-1]=hashmapVal[j-1]+10000;
			//hashmapKey[j-1]=hashmapKey[j]-1;
			hashmapVal[j]=hashmapVal[j]+10001;
			hashmapKey[j]=hashmapKey[j];

			found2=0;
			for(k:0..8){
				if
				:: found2==1->skip
				:: hashmapKey[k] == vec[i]+1 -> 
				hashmapVal[k]=hashmapVal[k]+10000;
				//printf("vel i+1 %d hashmapKey %d\n",vec[i]+1,hashmapKey[k]);
				found2=1
				:: timeout->skip
				fi
			}
			if
			:: found2==0->
			//printf("vel i+1 nao encontrado\n");
			hashmapKey[countKey]=vec[i]+1;
			countKey++;
			hashmapVal[countVal]=10000;
			countVal++;
			:: else->skip
			fi;

			//hashmapVal[j+1]=hashmapVal[j+1]+10000;
			//hashmapKey[j+1]=hashmapKey[j]+1;
			found=1;
			//printf("encontrou\n")
			:: timeout->skip
			fi

		}
		//printf("nao encontrou %d, vai agora para o if\n",vec[i]);
		if
		:: found==0->
		found2=0;
		for(k:0..8){
			if
			:: found2==1->skip
			:: hashmapKey[k] == vec[i]-1 -> 
			hashmapVal[k]=hashmapVal[k]+10000;
			//printf("vel i-1 %d hashmapKey %d\n",vec[i]-1,hashmapKey[k]);
			found2=1
			:: timeout->skip
			fi
		}
		if
		:: found2==0->
		//printf("vel i-1 nao encontrado\n");
		hashmapKey[countKey]=vec[i]-1;
		countKey++;
		hashmapVal[countVal]=10000;
		countVal++;
		:: else->skip
		fi;

		//hashmapKey[countKey]=vec[i]-1;
		//countKey++;
		//hashmapVal[countVal]=10000;
		//countVal++;
		hashmapKey[countKey]=vec[i];
		countKey++;
		hashmapVal[countVal]=10001;
		countVal++;

		found2=0;
		for(k:0..8){
			if
			:: found2==1->skip
			:: hashmapKey[k] == vec[i]+1 -> 
			hashmapVal[k]=hashmapVal[k]+10000;
			//printf("vel i-1 %d hashmapKey %d\n",vec[i]+1,hashmapKey[k]);
			found2=1
			:: timeout->skip
			fi
		}
		if
		:: found2==0->
		//printf("vel i+1 nao encontrado\n");
		hashmapKey[countKey]=vec[i]+1;
		countKey++;
		hashmapVal[countVal]=10000;
		countVal++;
		:: else->skip
		fi;

		//hashmapKey[countKey]=vec[i]+1;
		//countKey++;
		//hashmapVal[countVal]=10000;
		//countVal++;
		//printf("nao encontrou, adicinou %d\n",vec[i])
		:: else->
		//printf("found1 depois de found0\n");
		skip
		fi;
		
		//printf("resultados so far: \n");

		/*for(j:0..8){
			printf("%d %d\n",hashmapKey[j],hashmapVal[j]);
		}*/
	}

	for(j:0..8){
			printf("%d %d\n",hashmapKey[j],hashmapVal[j]);
	}

	majorCount=0;
	majorResult=0;
	maiority=0;
	found2=0;
	//printf("contar majorCount\n");

	for(i : 0..8){
		//printf("compare: %d %d\n",hashmapVal[i],majorCount);
		found2=0;
		for(j:0..2)
		{
			//printf("compare: %d %d\n",vec[j],hashmapKey[i]);
			if
			:: found2 == 1 -> skip
			:: vec[j]==hashmapKey[i]->found2=1
			:: else->skip
			:: timeout->skip
			fi
		}
		printf("found = %d hashmapKey = %d\n",found2,hashmapKey[i]);
		if
		:: found2==0->printf("found = 0\n");skip
		:: found2==1->
			if 
			:: hashmapVal[i]>majorCount->
				majorCount=hashmapVal[i];
				majorResult=hashmapKey[i];
				maiority=1;
			:: else->skip
			fi;
		:: hashmapVal[i]==majorCount->
		maiority=0;
		::else->skip;
		fi

	}

	//printf("MAJOR RESULT %d\n", majorResult);

	if
	:: maiority==1->
	printf("MAJOR RESULT %d\n",majorResult);
	:: else->
	printf("-1\n");
	fi;
}
