function Z=hari2periode(vektor,x)
%fungsi untuk mengubah data rerata harian menjadi data rerata periode hari
%tertentu misalnya 7 hari (untuk mingguan), atau 10 hari-an
%每隔x个数取这x个数的平均值 如vector=[1 2 3 4 5 6 7 8 9]T，
%则 a=hari2periode(vector,3)，a=[2 5 8]T，、
% a=hari2periode(vector,2)，a=[1.5 3.5 5.5 8]T
[m,n]=size(vektor);
a=floor(m/x);
for i=1:x*(a-1)
    for j=1:a-1
        Z(j,:)=mean(vektor((j-1)*x+1:j*x));
    end
end
    Z(a,:)=mean(vektor(x*(a-1)+1:m));
Z;
    
