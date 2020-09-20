function r=autoCorrSample(X,k)
%calculate autocorrelations of sequences which are columns of a matrix£¬while lag=k
%every column is a stational process
[m n]=size(X)
r=zeros(1,n);
if(k<m)
    for i=1:n
        x=X(:,i);
        x1=x(1:m-k,1);
        x2=x(k+1:m,1);
        corr=corrcoef(x1,x2);
        [m1 n1]=size(corr);
        if(m1<2)
            r(1,i)=corr(1,1);
        else
            r(1,i)=corr(2,1);
        end
    end
end;
r;

