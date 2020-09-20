%W-H reverse transform while Cs<2
% Yt is a sequence that has been standalized
function Z=WHReverse(X)
Y=standalize(X);
Cs=skewnessCoef(X);
CY=bsxfun(@times, Y,Cs);
Z=bsxfun(@plus, bsxfun(@rdivide,((CY/2+1).^(1/3)-1)*6,Cs),Cs/6);
Z;