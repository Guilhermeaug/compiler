%program
integer a, b, c, maior;
init
    read(a);
    read(b);
    read(c);

    maior = 0;

    %Calcula a maior idade
        if ( a>b and a>c ) then
    maior := a;
    else
        if (b>c) then
            maior := b;
        else
            maior := c;
        end
    end

    write({Maior idade: );
    write(maior);
return