% stadalize the seasonal hydrologu sequence to eliminate the influence of 
% mean and variance 
function Z=standalize(X)
Y=bsxfun(@minus, X, mean(X));%every elements in a column substract mean of the column
Z=bsxfun(@rdivide, Y, std(X));%every elements in a column of Y divide std of the column in X
Z;