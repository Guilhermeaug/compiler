App pessoa
  var
    integer cont;
    real altura, soma;
init
    cont := 5;
    soma := 0;
    repeat
        write({Altura: });
        read (altura);
        soma := soma  altura;
        cont := cont - 1
    until(cont=0);
    media := soma / 5;
    return;
    write({Media: });
    write (media)