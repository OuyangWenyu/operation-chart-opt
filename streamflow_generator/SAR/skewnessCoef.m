function [ Cs ] = skewnessCoef( X )
%SKEWNESSCOEF compute skewness coefficient of every column according to a sample
%   Detailed explanation goes here
Y=bsxfun(@minus, X, mean(X));%every elements in a column substract mean of the column
Z=sum(Y.^3);
s=std(X);
[m,n]=size(X);
Cs=Z./(s.^3)/(m-3);
end

