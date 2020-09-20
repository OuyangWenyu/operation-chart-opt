function Z=urutbuangnan(file)
%fungsi ambil file excel, yang NaN dibuang, kemudian dibuat sebagai
%sebuah array urut
A=xlsread(file);
B=vektorderetbaris(A);
[m,n]=size(B);
c=1;
for i=1:m
    if isnan(B(i))==0
       notnan(c,:)=B(i) ;
       c=c+1;
    end
end
Z=notnan;