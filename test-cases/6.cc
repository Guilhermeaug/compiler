%program
app Teste6
var
    integer a, b, c, maior;
    real z
init
  read(a);
  read(b);
  read(c);

  maior := 0;

  %Calcula a maior idade
  if ( a>b && a>c ) then
    maior := a
  else
    if (b>c) then
      maior := b
    else
      maior := c
    end
  end;

  write({Maior idade: });
  write(maior)
return
