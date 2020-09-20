function Z=vektorderetbaris(matriks)
%untuk membuat matriks menjadi kolom berderet sesuai urutan barisnya

[m,n]=size(matriks);
B=[];
for i=1:m
    C=matriks(i,:);
    B=horzcat(B,C);
end
Z=B';