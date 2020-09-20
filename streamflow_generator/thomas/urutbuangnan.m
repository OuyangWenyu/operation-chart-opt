function Z=urutbuangnan(file)
%fungsi ambil file excel, yang NaN dibuang, kemudian dibuat sebagai
%sebuah array urut
%从excel文件中读取数据矩阵（Excel格式可以是xls、xlsx等），然后转换成列向量，最后把数值存入向量Z
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