#define N 3


int vec[N];
int hashmapKey[9];
int hashmapVal[9];

active proctype voter(){
	int i,j,k,countKey,countVal,found,counter,found2;
	int majorCount,majorResult,maiority;
	countKey=0;
	countVal=0;
	counter=0;

	//for(i : 0..2){
		//select(j : 1..51);
		//counter++;
		//vec[i] = j
	//}
	vec[0]=27;
	vec[1]=25;
	vec[2]=23;
	counter=3;
	printf("vec = %d %d %d counter = %d\n",vec[0],vec[1],vec[2],counter);
	
	if
	:: counter<=1->printf("-2\n")
	:: else->printf("counter>1\n");skip
	fi;	

	for(i : 0..2){
		found=0;
		printf("Resultado: %d\n", vec[i]);
		for(j:0..8){
			//printf("ciclo 0..8 -> %d\n",j);
			//printf("compare %d %d\n",hashmapKey[j] , vec[i]);
			if
			:: found==1->printf("found=1\n");skip
			:: hashmapKey[j] == vec[i] -> printf("encontra\n");
				found2=0;
				for(k:0..8){
					if
					:: found2==1->skip
					:: hashmapKey[k] == vec[i]-1 -> 
					hashmapVal[k]=hashmapVal[k]+10000;
					printf("vel i-1 %d hashmapKey %d\n",vec[i]-1,hashmapKey[k]);
					found2=1
					:: timeout->skip
					fi
				}
				if
				:: found2==0->
				printf("vel i-1 nao encontrado\n");
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
					printf("vel i+1 %d hashmapKey %d\n",vec[i]+1,hashmapKey[k]);
					found2=1
					:: timeout->skip
					fi
				}
				if
				:: found2==0->
				printf("vel i+1 nao encontrado\n");
				hashmapKey[countKey]=vec[i]+1;
				countKey++;
				hashmapVal[countVal]=10000;
				countVal++;
				:: else->skip
				fi;

				//hashmapVal[j+1]=hashmapVal[j+1]+10000;
				//hashmapKey[j+1]=hashmapKey[j]+1;
				found=1;
				printf("encontrou\n")
			:: timeout->skip
			fi

		}
		printf("nao encontrou %d, vai agora para o if\n",vec[i]);
		if
		:: found==0->
				found2=0;
				for(k:0..8){
					if
					:: found2==1->skip
					:: hashmapKey[k] == vec[i]-1 -> 
					hashmapVal[k]=hashmapVal[k]+10000;
					printf("vel i-1 %d hashmapKey %d\n",vec[i]-1,hashmapKey[k]);
					found2=1
					:: timeout->skip
					fi
				}
				if
				:: found2==0->
				printf("vel i-1 nao encontrado\n");
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
					printf("vel i-1 %d hashmapKey %d\n",vec[i]+1,hashmapKey[k]);
					found2=1
					:: timeout->skip
					fi
				}
				if
				:: found2==0->
				printf("vel i+1 nao encontrado\n");
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
				printf("nao encontrou, adicinou %d\n",vec[i])
		:: else->printf("found1 depois de found0\n");skip
		fi;
		
		printf("resultados so far: \n");

		for(j:0..8){
			printf("%d %d\n",hashmapKey[j],hashmapVal[j]);
		}
	}


	majorCount=0;
	majorResult=0;
	maiority=0;
	printf("contar majorCount\n");

	for(i : 0..8){
		printf("compare: %d %d\n",hashmapVal[i],majorCount);
		if
		:: hashmapVal[i]>majorCount->
		majorCount=hashmapVal[i];
		majorResult=hashmapKey[i];
		maiority=1;
		:: hashmapVal[i]==majorCount->
		maiority=0;
		::else->skip;
		fi

	}

	printf("MAJOR RESULT %d\n", majorResult);

	if
	:: maiority==1->
	printf("%d\n",majorResult);
	:: else->
	printf("-1\n");
	fi;
}