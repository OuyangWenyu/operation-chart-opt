m=input('Input the number of stations: ');
n=input('Input the number of simulation years: ');
l=input('Input the number of periods in a year,12/36[12]: ');
path= input('Input path of the file: ','s');
sheetsNum=(1:m);
for i=1:m
    sheet=['Sheet', num2str(sheetsNum(i))];
    Xi=readData(path,sheet);
    Zi=WHReverse(Xi);
    if i==1
        [nx lx]=size(Xi);
        X=zeros(nx,lx,m);
        Z=zeros(nx,lx,m);
    end
    X(:,:,i)=Xi;
    Z(:,:,i)=Zi;
    Csxi=skewnessCoef(Xi);
    fprintf(['第',num2str(i),'电站的各个月径流的Cs值:\n']);
    fprintf([num2str(Csxi),'\n']);
end
[A B]=MSAR1Para(Z);%A m*m*l matrix
epsilon=randn(m,l,n);
z=MSAR1( A,B,epsilon,m,n,l);
x=zeros(n,l,m);
pathOut= input('Output path of the result:[without .xlsx]: \n','s');
pathOut=[pathOut,datestr(now,30),'.xlsx'];
for i=1:m
    CsX=skewnessCoef(X(:,:,i));
    y=WH(z(:,:,i),CsX);
    muX=mean(X(:,:,i));
    stdX= std(X(:,:,i));
    x(:,:,i)=standReverse(y,muX,stdX);
    sheet=['Sheet', num2str(sheetsNum(i))];
    xlswrite(pathOut,x(:,:,i),sheet);
end
