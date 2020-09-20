function Z=hari2periode(vektor,x)
%fungsi untuk mengubah data rerata harian menjadi data rerata periode hari
%tertentu misalnya 7 hari (untuk mingguan), atau 10 hari-an
[m,n]=size(vektor);
a=floor(m/x);
for i=1:x*(a-1)
    for j=1:a-1
        Z(j,:)=mean(vektor((j-1)*x+1:j*x));
    end
end
    Z(a,:)=mean(vektor(x*(a-1)+1:m));
Z;
    
