function [ X ] = standReverse( Y ,muX,stdX)
%STANDREVERSE reverse standalization
%   muX is a row vector,in which every element means a mean of a
%   month/decad,stdX is standard deviation
X_mu=bsxfun(@times, Y,stdX);
X=bsxfun(@plus, X_mu,muX);
X;

