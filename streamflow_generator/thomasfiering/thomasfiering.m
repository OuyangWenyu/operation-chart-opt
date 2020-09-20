function Z=thomasfiering(n_data,n_day,n_per,str1,str2)
%fungsi untuk mencari data bangkitan menggunakan thomas-fiering
%n_day adalah jumlah hari per satu tahap
%per_bangkit adalah periode (kalau n_hari=7, dalam setahun akan ada 
%floor(365/7) atau sekitar 52 tahap. n_per adalah jumlah tahun dibangkitkan
%atau total tahap sama dengan 53*n_per.
%n_data表示几年的数据，每年的数据以12行，每行表示一个月，放在一个excel里面，用月径流，则n_day就以30为例
A=tumpukgabungfile(n_data,n_day,str1);%n_data年的数据，每n_day取一个平均数，一年的数成一行，构成一个矩阵

%pembangkitan selama periode bangkit per_bangkit
AX=A;
for j=1:n_per%n_per是决定生成多少年的模拟径流
[m,n]=size(A);
[q_mean,q_std,rj,bj]=mat_thomas(A);%thomasfiering公式，rj就相当于beta_t
xj=sqrt(1-rj.^2);
t=rand(1,n);%标准正态分布变量t
    for i=1:n
        if i==1
        B(:,i)=q_mean(i)+(bj(n)*(AX(n)-q_mean(n)))+(t(n)*q_std(i)*t(i)*xj(n));    
        else
        B(:,i)=q_mean(i)+(bj(i-1)*(B(i-1)-q_mean(i-1)))+(t(i-1)*q_std(i)*t(i)*xj(i-1));
        end
    end
AX=vertcat(AX,B);
end
Z=AX;%最后得到各个B值，前面的数年是原数据，后面B累计的是新生成的
ZX=vektorderetbaris(Z);%把所有数据排成一列向量，是为了绘图

%memasukkan matriks pembanding
sprintf('===memasukkan file pembanding===')
Y=tumpukgabungfile(n_per,n_day,str2);
YY=vertcat(A,Y);
YX=vektorderetbaris(YY);
%把原数据取出来
[o,p]=size(ZX);
N=[1:o]';
M=[N,ZX,YX]
plot(N,ZX,N,YX)%绘图看效果
