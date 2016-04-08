function representation = asNaryRepresentation(base, v, rep_digits)
%ASNARYREPRESENTATION get the representation of a certain number v in a
%given base number system using a fixed number of digets for its
%representation.
%
% @param base number system basis
% @param v number in base 10 that should be tranformed
% @param rep_digits number of digits that can be used.
%
%   Detailed explanation goes here
% >> asNaryRepresentation(2, 10, 5)
% ans = 0 1 0 1 0
%
% >> asNaryRepresentation(3, 4)
% ans = 0 1 1

    digits = 3;
    if nargin == 3
        digits = rep_digits;
    end
    
    idx = digits;
    representation = zeros(1,digits);
    for pos=digits-1:-1:0
        co = 0;
        for t=1:digits-1
            if v - (base^pos) >= 0
                co = co + 1;
            else
                break;
            end
            v = v - (base^pos);
        end
        representation(digits-idx+1) = co;
        idx = idx - 1;
    end
end
