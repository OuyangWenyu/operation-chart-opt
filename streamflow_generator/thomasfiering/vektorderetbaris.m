function Z=vektorderetbaris(matriks)
%untuk membuat matriks menjadi kolom berderet sesuai urutan barisnya
%把矩阵各行排在一行，然后取转置，e.g. [1 2;3 4]->[1 2 3 4]的转置矩阵
[m,n]=size(matriks);
B=[];
for i=1:m
    C=matriks(i,:);
    B=horzcat(B,C);
end
Z=B';