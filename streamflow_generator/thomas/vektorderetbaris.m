function Z=vektorderetbaris(matriks)
%untuk membuat matriks menjadi kolom berderet sesuai urutan barisnya
%�Ѿ����������һ�У�Ȼ��ȡת�ã�e.g. [1 2;3 4]->[1 2 3 4]��ת�þ���
[m,n]=size(matriks);
B=[];
for i=1:m
    C=matriks(i,:);
    B=horzcat(B,C);
end
Z=B';