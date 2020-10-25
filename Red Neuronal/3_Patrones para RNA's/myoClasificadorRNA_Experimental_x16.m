%C:\Users\Sora\Dropbox\__Escuela__\[_Tesis_]\xTamo\RNA\Intento_7_F
clc
clear all;

%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%

%% Patrones de Letras -----------------------------------------------------%
patronA = csvread('PatronesEMG_x16/---_x16_Letter_A.csv');
patronB = csvread('PatronesEMG_x16/---_x16_Letter_B.csv');
patronC = csvread('PatronesEMG_x16/---_x16_Letter_C.csv');
patronD = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_D.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_D_Extra_1.csv') );
patronE = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_E.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_E_Extra_1.csv') );
patronG = csvread('PatronesEMG_x16/---_x16_Letter_G.csv');
patronH = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_H.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_H_Extra_1.csv') );
patronI = csvread('PatronesEMG_x16/---_x16_Letter_I.csv');
patronL = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_L.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_L_Extra_1.csv') );
patronM = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_M.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_M_Extra_1.csv') );
patronN = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_N.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_N_Extra_1.csv') );
patronO = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_O.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_O_Extra_1.csv') );
patronP = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_P.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_P_Extra_1.csv') );
patronR = csvread('PatronesEMG_x16/---_x16_Letter_R.csv');
patronS = csvread('PatronesEMG_x16/---_x16_Letter_S.csv');
patronT = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_T.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_T_Extra_1.csv') );
patronU = vertcat( csvread('PatronesEMG_x16/---_x16_Letter_U.csv'), csvread('PatronesEMG_x16/Extras/---_x16_Letter_U_Extra_1.csv') );

%% Entradas por paquete de letras -----------------------------------------%
entradasRNA_Pack1 = vertcat(patronA, patronL);
entradasRNA_Pack2 = vertcat(patronG, patronH);
entradasRNA_Pack3 = vertcat(patronE, patronB);
entradasRNA_Pack4 = vertcat(patronM, patronN);
entradasRNA_Pack5 = vertcat(patronC, patronO, patronD, patronP);
entradasRNA_Pack6 = vertcat(patronU, patronR, patronI, patronT, patronS);

%% Salidas por paquete de letras (Clasificacion)---------------------------%
classA =[];
classB =[];
classC =[];
classD =[];
classE =[];
classG =[];
classH =[];
classI =[];
classL =[];
classM =[];
classN =[];
classO =[];
classP =[];
classR =[];
classS =[];
classT =[];
classU =[];

[alto,largo] = size(patronA);
for i=1:alto
    classA = vertcat(classA,[1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronB);
for i=1:alto
    classB = vertcat(classB,[0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronC);
for i=1:alto
    classC = vertcat(classC,[0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronD);
for i=1:alto
    classD = vertcat(classD,[0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronE);
for i=1:alto
    classE = vertcat(classE,[0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronG);
for i=1:alto
    classG = vertcat(classG,[0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronH);
for i=1:alto
    classH = vertcat(classH,[0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronI);
for i=1:alto
    classI = vertcat(classI,[0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronL);
for i=1:alto
    classL = vertcat(classL,[0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronM);
for i=1:alto
    classM = vertcat(classM,[0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0]);
end
[alto,largo] = size(patronN);
for i=1:alto
    classN = vertcat(classN,[0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0]);
end
[alto,largo] = size(patronO);
for i=1:alto
    classO = vertcat(classO,[0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0]);
end
[alto,largo] = size(patronP);
for i=1:alto
    classP = vertcat(classP,[0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0]);
end
[alto,largo] = size(patronR);
for i=1:alto
    classR = vertcat(classR,[0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0]);
end
[alto,largo] = size(patronS);
for i=1:alto
    classS = vertcat(classS,[0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0]);
end
[alto,largo] = size(patronT);
for i=1:alto
    classT = vertcat(classT,[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0]);
end
[alto,largo] = size(patronU);
for i=1:alto
    classU = vertcat(classU,[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1]);
end

salidasRNA_Pack1 = vertcat(classA, classL);
salidasRNA_Pack2 = vertcat(classG, classH);
salidasRNA_Pack3 = vertcat(classE, classB);
salidasRNA_Pack4 = vertcat(classM, classN);
salidasRNA_Pack5 = vertcat(classC, classO, classD, classP);
salidasRNA_Pack6 = vertcat(classU, classR, classI, classT, classS);

%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%

%% Entradas por zona (Clasificacion)---------------------------------------%
entradasRNA_AllZones = vertcat(entradasRNA_Pack1,entradasRNA_Pack2,entradasRNA_Pack3,entradasRNA_Pack4,entradasRNA_Pack5,entradasRNA_Pack6);

%% Salidas por zona (Clasificacion)----------------------------------------%
cPack1 = [];
cPack2 = [];
cPack3 = [];
cPack4 = [];
cPack5 = [];
cPack6 = [];

[alto,largo] = size(entradasRNA_Pack1);
for i=1:alto
    cPack1 = vertcat(cPack1,[1,0,0,0,0,0]);
end
[alto,largo] = size(entradasRNA_Pack2);
for i=1:alto
    cPack2 = vertcat(cPack2,[0,1,0,0,0,0]);
end
[alto,largo] = size(entradasRNA_Pack3);
for i=1:alto
    cPack3 = vertcat(cPack3,[0,0,1,0,0,0]);
end
[alto,largo] = size(entradasRNA_Pack4);
for i=1:alto
    cPack4 = vertcat(cPack4,[0,0,0,1,0,0]);    
end
[alto,largo] = size(entradasRNA_Pack5);
for i=1:alto
    cPack5 = vertcat(cPack5,[0,0,0,0,1,0]);
end
[alto,largo] = size(entradasRNA_Pack6);
for i=1:alto
    cPack6 = vertcat(cPack6,[0,0,0,0,0,1]);
end

salidasRNA_AllZones = vertcat(cPack1,cPack2,cPack3,cPack4,cPack5,cPack6);

%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%

%% RNA por Zona -----------------------------------------------------------%
%-------------- Parametros
x=entradasRNA_AllZones';
y=salidasRNA_AllZones';
rango_entrada=minmax(x);
%-------------- Entrenamiento
rnaMyo_Zones=newff(rango_entrada,[10 6],{'logsig','logsig'},'trainlm');
rnaMyo_Zones.trainParam.show=1;
rnaMyo_Zones.trainParam.epochs = 1500;
rnaMyo_Zones.trainParam.goal = 0;
rnaMyo_Zones = train(rnaMyo_Zones,x,y);
%-------------- Resultados
zoneW1 = rnaMyo_Zones.IW{1,1};
zoneW2 = rnaMyo_Zones.LW{2,1};
zoneB1 = rnaMyo_Zones.b{1};
zoneB2 = rnaMyo_Zones.b{2};
%-------------- Resultados (en Cadena para Android)
zoneW1_string = '';
zoneW2_string = '';
zoneB1_string = '';
zoneB2_string = '';
[alto,largo] = size(zoneW1);
for i=1:alto
    for j=1:largo
        auxValue = num2str(zoneW1( i, j));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        zoneW1_string = strcat(zoneW1_string,auxValue);
    end
    if (i) < (alto)
        zoneW1_string = strcat(zoneW1_string,'|');
    end
end
[alto,largo] = size(zoneW2);
for i=1:alto
    for j=1:largo
        auxValue = num2str(zoneW2( i , j ));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        zoneW2_string = strcat(zoneW2_string,auxValue);
    end
    if (i) < (alto)
        zoneW2_string = strcat(zoneW2_string,'|');
    end
end
[alto,largo] = size(zoneB1);
for i=1:alto
    auxValue = num2str(zoneB1( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    zoneB1_string = strcat(zoneB1_string,auxValue);
end
[alto,largo] = size(zoneB2);
for i=1:alto
    auxValue = num2str(zoneB2( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    zoneB2_string = strcat(zoneB2_string,auxValue);
end

%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%

%% RNA por Paquetes -------------------------------------------------------%

%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%

%% RNA por Pack1 -------------------------------------------------------%
%-------------- Parametros
x=entradasRNA_Pack1';
y=salidasRNA_Pack1';
rango_entrada=minmax(x);
%-------------- Entrenamiento
rnaMyo_Pack1=newff(rango_entrada,[10 17],{'logsig','logsig'},'trainlm');
rnaMyo_Pack1.trainParam.show=1;
rnaMyo_Pack1.trainParam.epochs = 1500;
rnaMyo_Pack1.trainParam.goal = 0;
rnaMyo_Pack1 = train(rnaMyo_Pack1,x,y);
%-------------- Resultados
pack1W1 = rnaMyo_Pack1.IW{1,1};
pack1W2 = rnaMyo_Pack1.LW{2,1};
pack1B1 = rnaMyo_Pack1.b{1};
pack1B2 = rnaMyo_Pack1.b{2};
%-------------- Resultados (en Cadena para Android)
pack1W1_string = '';
pack1W2_string = '';
pack1B1_string = '';
pack1B2_string = '';
[alto,largo] = size(pack1W1);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack1W1( i, j));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack1W1_string = strcat(pack1W1_string,auxValue);
    end
    if (i) < (alto)
        pack1W1_string = strcat(pack1W1_string,'|');
    end
end
[alto,largo] = size(pack1W2);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack1W2( i , j ));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack1W2_string = strcat(pack1W2_string,auxValue);
    end
    if (i) < (alto)
        pack1W2_string = strcat(pack1W2_string,'|');
    end
end
[alto,largo] = size(pack1B1);
for i=1:alto
    auxValue = num2str(pack1B1( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack1B1_string = strcat(pack1B1_string,auxValue);
end
[alto,largo] = size(pack1B2);
for i=1:alto
    auxValue = num2str(pack1B2( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack1B2_string = strcat(pack1B2_string,auxValue);
end

%--------------------------------------------------------------------------%

%% RNA por Pack2 -------------------------------------------------------%
%-------------- Parametros
x=entradasRNA_Pack2';
y=salidasRNA_Pack2';
rango_entrada=minmax(x);
%-------------- Entrenamiento
rnaMyo_Pack2=newff(rango_entrada,[10 17],{'logsig','logsig'},'trainlm');
rnaMyo_Pack2.trainParam.show=1;
rnaMyo_Pack2.trainParam.epochs = 1500;
rnaMyo_Pack2.trainParam.goal = 0;
rnaMyo_Pack2 = train(rnaMyo_Pack2,x,y);
%-------------- Resultados
pack2W1 = rnaMyo_Pack2.IW{1,1};
pack2W2 = rnaMyo_Pack2.LW{2,1};
pack2B1 = rnaMyo_Pack2.b{1};
pack2B2 = rnaMyo_Pack2.b{2};
%-------------- Resultados (en Cadena para Android)
pack2W1_string = '';
pack2W2_string = '';
pack2B1_string = '';
pack2B2_string = '';
[alto,largo] = size(pack2W1);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack2W1( i, j));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack2W1_string = strcat(pack2W1_string,auxValue);
    end
    if (i) < (alto)
        pack2W1_string = strcat(pack2W1_string,'|');
    end
end
[alto,largo] = size(pack2W2);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack2W2( i , j ));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack2W2_string = strcat(pack2W2_string,auxValue);
    end
    if (i) < (alto)
        pack2W2_string = strcat(pack2W2_string,'|');
    end
end
[alto,largo] = size(pack2B1);
for i=1:alto
    auxValue = num2str(pack2B1( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack2B1_string = strcat(pack2B1_string,auxValue);
end
[alto,largo] = size(pack2B2);
for i=1:alto
    auxValue = num2str(pack2B2( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack2B2_string = strcat(pack2B2_string,auxValue);
end

%--------------------------------------------------------------------------%

%% RNA por Pack3 -------------------------------------------------------%
%-------------- Parametros
x=entradasRNA_Pack3';
y=salidasRNA_Pack3';
rango_entrada=minmax(x);
%-------------- Entrenamiento
rnaMyo_Pack3=newff(rango_entrada,[10 17],{'logsig','logsig'},'trainlm');
rnaMyo_Pack3.trainParam.show=1;
rnaMyo_Pack3.trainParam.epochs = 1500;
rnaMyo_Pack3.trainParam.goal = 0;
rnaMyo_Pack3 = train(rnaMyo_Pack3,x,y);
%-------------- Resultados
pack3W1 = rnaMyo_Pack3.IW{1,1};
pack3W2 = rnaMyo_Pack3.LW{2,1};
pack3B1 = rnaMyo_Pack3.b{1};
pack3B2 = rnaMyo_Pack3.b{2};
%-------------- Resultados (en Cadena para Android)
pack3W1_string = '';
pack3W2_string = '';
pack3B1_string = '';
pack3B2_string = '';
[alto,largo] = size(pack3W1);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack3W1( i, j));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack3W1_string = strcat(pack3W1_string,auxValue);
    end
    if (i) < (alto)
        pack3W1_string = strcat(pack3W1_string,'|');
    end
end
[alto,largo] = size(pack3W2);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack3W2( i , j ));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack3W2_string = strcat(pack3W2_string,auxValue);
    end
    if (i) < (alto)
        pack3W2_string = strcat(pack3W2_string,'|');
    end
end
[alto,largo] = size(pack3B1);
for i=1:alto
    auxValue = num2str(pack3B1( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack3B1_string = strcat(pack3B1_string,auxValue);
end
[alto,largo] = size(pack3B2);
for i=1:alto
    auxValue = num2str(pack3B2( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack3B2_string = strcat(pack3B2_string,auxValue);
end

%--------------------------------------------------------------------------%

%% RNA por Pack4 -------------------------------------------------------%
%-------------- Parametros
x=entradasRNA_Pack4';
y=salidasRNA_Pack4';
rango_entrada=minmax(x);
%-------------- Entrenamiento
rnaMyo_Pack4=newff(rango_entrada,[10 17],{'logsig','logsig'},'trainlm');
rnaMyo_Pack4.trainParam.show=1;
rnaMyo_Pack4.trainParam.epochs = 1500;
rnaMyo_Pack4.trainParam.goal = 0;
rnaMyo_Pack4 = train(rnaMyo_Pack4,x,y);
%-------------- Resultados
pack4W1 = rnaMyo_Pack4.IW{1,1};
pack4W2 = rnaMyo_Pack4.LW{2,1};
pack4B1 = rnaMyo_Pack4.b{1};
pack4B2 = rnaMyo_Pack4.b{2};
%-------------- Resultados (en Cadena para Android)
pack4W1_string = '';
pack4W2_string = '';
pack4B1_string = '';
pack4B2_string = '';
[alto,largo] = size(pack4W1);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack4W1( i, j));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack4W1_string = strcat(pack4W1_string,auxValue);
    end
    if (i) < (alto)
        pack4W1_string = strcat(pack4W1_string,'|');
    end
end
[alto,largo] = size(pack4W2);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack4W2( i , j ));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack4W2_string = strcat(pack4W2_string,auxValue);
    end
    if (i) < (alto)
        pack4W2_string = strcat(pack4W2_string,'|');
    end
end
[alto,largo] = size(pack4B1);
for i=1:alto
    auxValue = num2str(pack4B1( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack4B1_string = strcat(pack4B1_string,auxValue);
end
[alto,largo] = size(pack4B2);
for i=1:alto
    auxValue = num2str(pack4B2( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack4B2_string = strcat(pack4B2_string,auxValue);
end

%--------------------------------------------------------------------------%

%% RNA por Pack5 -------------------------------------------------------%
%-------------- Parametros
x=entradasRNA_Pack5';
y=salidasRNA_Pack5';
rango_entrada=minmax(x);
%-------------- Entrenamiento
rnaMyo_Pack5=newff(rango_entrada,[10 17],{'logsig','logsig'},'trainlm');
rnaMyo_Pack5.trainParam.show=1;
rnaMyo_Pack5.trainParam.epochs = 1500;
rnaMyo_Pack5.trainParam.goal = 0;
rnaMyo_Pack5 = train(rnaMyo_Pack5,x,y);
%-------------- Resultados
pack5W1 = rnaMyo_Pack5.IW{1,1};
pack5W2 = rnaMyo_Pack5.LW{2,1};
pack5B1 = rnaMyo_Pack5.b{1};
pack5B2 = rnaMyo_Pack5.b{2};
%-------------- Resultados (en Cadena para Android)
pack5W1_string = '';
pack5W2_string = '';
pack5B1_string = '';
pack5B2_string = '';
[alto,largo] = size(pack5W1);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack5W1( i, j));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack5W1_string = strcat(pack5W1_string,auxValue);
    end
    if (i) < (alto)
        pack5W1_string = strcat(pack5W1_string,'|');
    end
end
[alto,largo] = size(pack5W2);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack5W2( i , j ));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack5W2_string = strcat(pack5W2_string,auxValue);
    end
    if (i) < (alto)
        pack5W2_string = strcat(pack5W2_string,'|');
    end
end
[alto,largo] = size(pack5B1);
for i=1:alto
    auxValue = num2str(pack5B1( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack5B1_string = strcat(pack5B1_string,auxValue);
end
[alto,largo] = size(pack5B2);
for i=1:alto
    auxValue = num2str(pack5B2( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack5B2_string = strcat(pack5B2_string,auxValue);
end

%--------------------------------------------------------------------------%

%% RNA por Pack6 -------------------------------------------------------%
%-------------- Parametros
x=entradasRNA_Pack6';
y=salidasRNA_Pack6';
rango_entrada=minmax(x);
%-------------- Entrenamiento
rnaMyo_Pack6=newff(rango_entrada,[10 17],{'logsig','logsig'},'trainlm');
rnaMyo_Pack6.trainParam.show=1;
rnaMyo_Pack6.trainParam.epochs = 1500;
rnaMyo_Pack6.trainParam.goal = 0;
rnaMyo_Pack6 = train(rnaMyo_Pack6,x,y);
%-------------- Resultados
pack6W1 = rnaMyo_Pack6.IW{1,1};
pack6W2 = rnaMyo_Pack6.LW{2,1};
pack6B1 = rnaMyo_Pack6.b{1};
pack6B2 = rnaMyo_Pack6.b{2};
%-------------- Resultados (en Cadena para Android)
pack6W1_string = '';
pack6W2_string = '';
pack6B1_string = '';
pack6B2_string = '';
[alto,largo] = size(pack6W1);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack6W1( i, j));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack6W1_string = strcat(pack6W1_string,auxValue);
    end
    if (i) < (alto)
        pack6W1_string = strcat(pack6W1_string,'|');
    end
end
[alto,largo] = size(pack6W2);
for i=1:alto
    for j=1:largo
        auxValue = num2str(pack6W2( i , j ));
        if (j < largo)
            auxValue = strcat(auxValue, ',');
        end
        pack6W2_string = strcat(pack6W2_string,auxValue);
    end
    if (i) < (alto)
        pack6W2_string = strcat(pack6W2_string,'|');
    end
end
[alto,largo] = size(pack6B1);
for i=1:alto
    auxValue = num2str(pack6B1( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack6B1_string = strcat(pack6B1_string,auxValue);
end
[alto,largo] = size(pack6B2);
for i=1:alto
    auxValue = num2str(pack6B2( i, largo));
    if (i) < (alto)
        auxValue = strcat(auxValue,',');
    end
    pack6B2_string = strcat(pack6B2_string,auxValue);
end

%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%
%--------------------------------------------------------------------------%

fileID = fopen('PesosExperimentales/x16_PesosAllZones.rnadat','wt');
    fprintf(fileID,'%s\n',zoneW1_string,zoneW2_string,zoneB1_string,zoneB2_string);
fclose(fileID);

fileID = fopen('PesosExperimentales/x16_PesosPack1.rnadat','wt');
    fprintf(fileID,'%s\n',pack1W1_string,pack1W2_string,pack1B1_string,pack1B2_string);
fclose(fileID);

fileID = fopen('PesosExperimentales/x16_PesosPack2.rnadat','wt');
    fprintf(fileID,'%s\n',pack2W1_string,pack2W2_string,pack2B1_string,pack2B2_string);
fclose(fileID);

fileID = fopen('PesosExperimentales/x16_PesosPack3.rnadat','wt');
    fprintf(fileID,'%s\n',pack3W1_string,pack3W2_string,pack3B1_string,pack3B2_string);
fclose(fileID);

fileID = fopen('PesosExperimentales/x16_PesosPack4.rnadat','wt');
    fprintf(fileID,'%s\n',pack4W1_string,pack4W2_string,pack4B1_string,pack4B2_string);
fclose(fileID);

fileID = fopen('PesosExperimentales/x16_PesosPack5.rnadat','wt');
    fprintf(fileID,'%s\n',pack5W1_string,pack5W2_string,pack5B1_string,pack5B2_string);
fclose(fileID);

fileID = fopen('PesosExperimentales/x16_PesosPack6.rnadat','wt');
    fprintf(fileID,'%s\n',pack6W1_string,pack6W2_string,pack6B1_string,pack6B2_string);
fclose(fileID);