function D=buangnan(matriks)
%fungsi mbuang NaN
[m,n]=size(matriks);
c=1;
for i=1:m
    if isnan(matriks(i))==0
       notnan(c)=matriks(i) ;
       c=c+1;
    end
end
D=notnan';