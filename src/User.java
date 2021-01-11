import Bot.UserInfo;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User implements NativeKeyListener{

    static int backspace = 0;
    /* Key Pressed */
    public void nativeKeyPressed(NativeKeyEvent e) {
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        /* Terminate program when one press ESCAPE */
        if (e.getKeyCode() == NativeKeyEvent.VC_BACKSPACE) {
            backspace++;
        }
    }

    /* Key Released */
    public void nativeKeyReleased(NativeKeyEvent e) {
        // System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    /* can't find any output from this call */
    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + e.getKeyChar());
    }


    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException, NativeHookException {

        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        String message = "";
        //String sugestao = "";
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        long timeElapsed = 0;
        long start;
        long finish;
        String emotion = "";
        //int edition = 0;

        // Resumo
        File resumo = new File("Resumo.txt");
        if(resumo.exists()){
            resumo.delete();
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(resumo, true));
        String entrada = "";

        /* Cenas para iniciar o logger: */
        try {
            /* Register jNativeHook */
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            /* Its error */
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        // https://github.com/kwhat/jnativehook/blob/2.2/doc/ConsoleOutput.md
        // Get the logger for "com.github.kwhat.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
        GlobalScreen.addNativeKeyListener(new User());
        char c = 'i';
        Scanner scanner = new Scanner(System. in);

        System.out.print("Conversa com o Bot.ChatBot!\n");
        while(!(message.equalsIgnoreCase("adeus"))){
            start = System.currentTimeMillis();
            System.out.print(">");
            message = scanner. nextLine();
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;

            //establish socket connection to server
            socket = new Socket(host.getHostName(), 9876);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());

            emotion = decideEmotion(message, backspace, timeElapsed);
            UserInfo userInfo = new UserInfo(message, emotion);
            System.out.println("Estado: " + userInfo.getEmotion());
            oos.writeObject(userInfo);
            backspace = 0;

            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String response = (String) ois.readObject();

            while(c != 'n' && c != 'y') {
                System.out.println("Premir y/n para aceitar: [sugestão] " + response);
                c = scanner.next().charAt(0);
            }
            if (c == 'n') {
                System.out.println("Que resposta preferia que o ChatBot desse?");
                scanner.nextLine();
                response = scanner.nextLine();
                System.out.println("Bot.ChatBot: " + response);
            }else{
                scanner.nextLine();
                System.out.println("Bot.ChatBot: " + response);
            }
            //write entrada do Resumo
            entrada = "User ["+emotion+"]: "+message+";"+'\n'+"ChatBot: "+response+";"+"\n ----------------------------";
            out.write(entrada);
            out.newLine();
            c = 'i';
        }
        //close resources && shutdown keylogger
        GlobalScreen.unregisterNativeHook();

        ois.close();
        oos.close();
        socket.close();
        scanner.close();
        out.close();
    }

    public static String decideEmotion(String log, int backspaces, long timeelapsed){
        List<String> fel = Arrays.asList("absolvição","abundância","abundante","elogio","acompanhamento","realizar","realizado","alcançar","realização","acrobata","admirável","admiração","adorável","adoração","adorar","avançar","advento","advocacia","estética","afeição","afluência","vivo","fascinação","aloha","incrivelmente","ambição","amém","amigável","anistia","amour","divertir","divertido","divertimento","divertido","anjo","angélico","animado","aplauso","valorização","aprovar","ardente","arte","aspiração","aspirar","aspirador","assombro","expiar","auspicioso","autêntico","prêmio","bebê","bálsamo","banquete","batismal","basquete","praia","viga","radiante","embelezamento","bonita","embelezar","beleza","cerveja","ajudar","benevolência","benigno","desposada","nascimento","aniversário","abençoar","abençoado","bênção","bênçãos","felicidade","bem-aventurado","flor","flor","violento","prosperidade","bônus","caridoso","recompensa","buquê","nupcial","noiva","noivo","dama de honra","clarear","brilhante","fraternal","camarada","beijo","bezerro","cândido","cativar","Carol","dinheiro","catedral","célebre","a comemorar","celebração","celebridade","celestial","cerimônia","campeão","cântico","caridoso","caridade","Encantado","alegria","alegre","alegria","aplauso","alegre","acalentar","criança","infância","gorjeio","chocolate","coro","coral","cacarejar","igreja","civilizado","aplaudir","clássicos","limpo","clímax","proximidade","encerramento","palhaço","conforto","comemorar","comemoração","louvável","comunhão","companheiro","compensar","complemento","completando","realização","elogio","conciliação","confiança","confiante","congratulatório","conhecedor","consagração","conteúdo","coroação","namoro","enseada","berço","creme","crio","crescendo","coroação","chamego","dança","querido","filha","amanhecer","Combinado","delicioso","delícia","Encantado","delicioso","libertação","demonstrativo","destino","devoto","diamante","diário","cantiga","boneca","golfinho","pomba","ansioso","ânsia","êxtase","extático","edificação","ejaculação","exultante","elétrico","elegância","elegante","elevação","elite","libertação","abraçar","encantar","enchanted","encantador","encorajar","interminável","contratado","atraente","apreciar","desfrutando","iluminar","iluminação","animar","entreter","entretido","divertido","diversão","entusiasmo","entusiasta","igualdade","erótico","estabelecido","estima","sempre-viva","exaltar","exaltação","exaltado","ultrapassarem","sobressair","excelência","excelente","excitação","excitar","animado","excitação","emocionante","alegria","expediente","requintado","exuberância","fain","fé","familiaridade","chique","fanfarra","favorável","favorito","façanha","sentindo-me","felicidade","fervor","festival","festivo","festa","fidelidade","fiesta","finalmente","primogênito","apropriado","lisonjeiro","flerte","carinho","Comida","futebol","antepassados","fortitude","fortuna","encontrado","fraternal","liberdade","livremente","amigos","afabilidade","amigáveis","amizade","brincalhão","brincadeira","Preencha","cumprimento","Diversão","ganho","jardim","jóia","generosidade","generoso","genial","Presente","risadinha","feliz","alegria","alegria","deslize","vislumbre","brilho","glorificação","glorificar","glória","brilho","Deus","piedoso","dádiva","Bom","bondade","deslumbrante","graduação","netos","conceder","gratificar","gratidão","grandeza","verde","sorriso largo","crescer","jorro","felizmente","felicidade","feliz","ousado","harmoniosamente","harmonia","colheita","curar","cicatrização","saudável","sincero","cordialmente","celestial","céus","hedonismo","útil","herói","heróico","heroísmo","apogeu","mais alta","hilário","hilaridade","contratar","passatempo","feriado","santidade","honesto","lua de mel","esperança","esperançoso","abraço","humanitário","humanidade","engraçado","hurra","hino","iluminar","iluminação","imaculado","imergir","melhorar","melhoria","inauguração","renda","independência","infantil","infinito","herança","inseparável","inspiração","inspirar","inspirado","inteligência","intenso","íntimo","intimamente","convidar","convidativo","bolada","brincadeira","palhaço","jornada","jovial","alegria","alegre","alegre","jubilante","jubileu","saltar","tipo","parentes","beijo","gatinho","glória","trabalho","Cordeiro","riso","rindo","riso","louros","legalizado","lazer","libertar","libertação","liberdade","gosto","licor","adorável","Ame","adorável","fazer amor","amante","amoroso","leal","sorte","por sorte","melado","brilho","luxuoso","luxo","lira","lírico","mágico","magnificência","magnífico","majestoso","maioria","casamento","medula","casar","maravilhoso","maravilhosamente","massagem","Obra-prima","mestria","matrimônio","medalha","meditar","memorável","meritório","folguedo","alegre","parteira","poderoso","ministério","milagre","milagroso","alegria","dinheiro","moral","mãe","maternidade","música","musical","sesta","notável","berçário","nutrir","oásis","prestativo","obtenível","ópera","oportuno","otimismo","orquestra","ordenação","órgão","organização","orgasmo","explosão","excelente","radiante","desfile","parangona","paixão","apaixonado","pastor","pastelaria","pagar","Paz","pacífico","perfeito","perfeição","piquenique","pitoresco","brincalhão","recreio","teatro","agradável","satisfeito","agradável","penhor","possuir","poderoso","praticado","louvor","elogiou","louvável","rezar","precioso","presente","preservativo","prestígio","presto","bonita","prevalecer","orgulho","sacerdócio","principesco","privilegiado","procissão","proficiência","progresso","progressão","promessa","prosperar","próspero","orgulhoso","fornecendo","purificar","ronronar","singular","esplendor","radiante","extasiado","arrebatamento","delírio","delirante","prontidão","receber","reconciliação","recreação","recreativo","regozijar-se","regozijo","reacender","notável","remédio","renovação","reembolsar","reprodutivo","resgate","recursos","respeito","pausa","resplandecente","reconstituinte","aposentadoria","orgia","revels","reverenciar","reverência","reverendo","devaneio","renascimento","recompensa","rítmico","fita","Aumentar","roadster","brincalhão","romance","romântico","romantismo","sapatão","seguro","santo","santo","salário","salutar","saudação","salvação","santificação","santificar","santuário","satisfeito","salvar","sabor","bolsa de estudo","Ponto","sensacional","sensual","sensualidade","sensual","serenidade","sexo","compartilhar","brilhante","compras","bobo","simplificar","cantar","sorriso","sorridente","soneto","sonoro","calmante","solidez","estância termal","spaniel","centelha","especial","espíritos","esplêndido","esplendor","cônjuge","estrela","estrelado","esterlino","fortalecimento","sublimação","suceder","sucedendo","sucesso","bem sucedido","sol","ensolarado","luz do sol","super-homem","Super estrela","suporte","supremacia","surpresa","doce","amor","doces","nadar","simetria","compreensivo","sinfonia","sincronizar","tentadora","ensinar","proposta","ternura","grato","ação de graças","terapêutico","emoção","emocionante","próspera","cócegas","ouropel","torrada","tranquilo","tranqüilidade","transcendência","tesouro","tratar","árvore","triunfo","triunfante","troféu","trégua","verdade","cintilação","invicto","sem constrangimento","imortal","inesperado","unificação","sem igual","desatar","elevação","utópico","férias","venerável","veracidade","vernal","vencedor","vitorioso","vitória","vindicação","virtuoso","visionário","visitante","vitalidade","vivaz","vívido","voluntário","voluptuoso","voto","voto","salário","riqueza","peso","congratulou-","capricho","caprichoso","branco","brancura","vencedora","vencedor","ganhos","espirituoso","maravilhoso","maravilhosamente","adoração","ânsia","jovem","juventude","zelo","zeloso","entusiasmo");
        List<String> tri = Arrays.asList("abandonar","abandonado","abandono","abdução","aborto","abortivo","abscesso","ausência","ausente","absentista","Abuso","abismal","abismo","acidente","amaldiçoado","dor","doendo","víbora","à deriva","adultério","desfavoraveis","adversidade","afligir","aflição","afronta","resultado","agravante","agonia","afligir","doente","alcoolismo","alienado","alienação","anátema","ancoragem","angústia","animosidade","aniquilado","aniquilação","anulação","antraz","anti social","ansiedade","apático","apatia","desculpar","apendicite","árido","acusação","arsênico","arte","envergonhado","cinza","assaltante","assassino","assassinato","aterosclerose","atrocidade","atrofia","atacante","atenuação","austero","autópsia","avalanche","terrível","ressaca","bactérias","ruim","mal","estrondo","banir","banido","banimento","falido","falência","Banshee","estéril","Desgraçado","agredidas","batalhou","espancamento","implorar","mendigo","desmerecer","enlutado","perda","trair","traição","esquife","fanático","cadela","amargamente","amargura","preto","negrume","desabrigado","hemorragia","defeito","ferrugem","arruinada","cegamente","cegueira","bloqueio","derramamento de sangue","sangrento","azul","blues","mancada","bomba","bombardeiro","escravidão","tédio","incomodando","inferior","romper","quebrou","quebrado","brutamontes","bicho-papão","vagabundo","oneroso","enterro","enterrado","Burke","enterrar","cadáver","gaiola","calamidade","cancelar","câncer","cativo","cativeiro","carcaça","carcinoma","cardiomiopatia","carnificina","caso","caixão","acidente","catarata","catástrofe","cemitério","desgosto","caos","imputável","afogador","cólera","crônico","encerramento","nublado","nublado","cocaína","coerção","caixão","frieza","colapso","conluio","coma","comatoso","comemorar","compromisso","comunismo","queixar-se","esconder","preocupado","concussão","condenação","condescendência","pêsames","confissão","confinar","confinado","reclusão","confiscar","conflito","consagração","console","restrição","contaminado","presidiário","cadáver","corruptora","corse","sofá","covarde","colisão","louco","cremação","adular servilmente","aleijado","aleijado","crítica","criticar","Cruz","crucificação","cruel","crueldade","desmoronando","esmagado","chora","chorando","cripta","pesado","sangria","maldição","amaldiçoado","corte","cisto","citomegalovírus","demônio","dano","danos","danação","perigo","escuro","escurecer","escurecido","escuridão","pontilhada","mortal","morte","desastre","dívida","decadência","deteriorado","falecido","engano","enganador","enganar","decomposto","decomposição","padrão","derrotado","réu","sem defesa","esvaziar","deformado","deformidade","defunto","desafiar","degeneração","aviltante","atraso","delirante","delírio","dilúvio","ilusão","demência","falecimento","demolir","demônio","demoníaco","demonstrativo","desmoralizado","negado","afastar","partiu","partida","dependência","deplorável","deplorar","deportar","deportação","depravado","depreciados","deprimir","deprimido","deprimente","depressão","depressivo","privação","derrogação","depreciativo","descida","profanação","deserto","deserto","desolação","desespero","desesperando","despotismo","destino","desamparado","destruído","destruidor","detento","detenção","deteriorar","deteriorou-se","deterioração","devastar","devastador","devastação","desvio","diabo","ditadura","morrer","dificuldades","dificuldade","dilapidado","diminuir","horrendo","incapacidade","incapacitar","Desativado","discordando","desacordo","não permitido","desiludir","desapontado","decepcionante","desapontamento","desaprovação","desaprovar","desaprovava","desaprovando","desastre","desastroso","descolorido","desconforto","desligar","desligado","descontentamento","descontinuidade","desencorajar","discriminar","discriminação","doença","doente","desencarnada","desfigurado","desgraça","desonrado","descontente","desgosto","desmotivada","desanimador","desanimado","desonesto","desonra","desilusão","não gostou","deslocado","sombrio","desânimo","desmembramento","demissão","denegrir","depreciativo","disparidade","imparcial","dissipar","deslocado","Descontente","despossuídos","desqualificado","desqualificar","desrespeitoso","desserviço","dissolução","perturbado","aflição","perturbação","perturbado","divórcio","calmarias","doação","dor","dominação","condenado","Dia do julgamento","dúvida","ruína","monótono","terrível","terrivelmente","triste","afogar","drogado","embotar","dumps","coação","anão","morrendo","disenteria","terremoto","eliminação","emaciado","embaraçar","embaraço","embolia","emergência","vazio","estorvo","endêmico","interminável","endocardite","inimizade","escravizados","enredado","epidemia","epitáfio","erro","evitar","estima","eutanásia","evanescência","evasão","expulsar","despejo","mal","excluídos","Excluindo","exclusão","execução","carrasco","esgotado","exaustão","exílio","exorcismo","expelir","expirar","explodir","expulsão","exterminar","extinto","falta","falha","desmaio","Sem fé","outono","queda","fome","jejum","gordura","fatal","fatalidade","fatigado","gorduroso","falha","temeroso","medrosamente","débil","sentindo-me","caiu","feudalismo","flácido","falha","velo","vacilar","açoitar","fiasco","linguado","proibir","excluir","confiscar","confisco","esquecido","desamparado","abandonar","desamparado","fortaleza","frágil","fragilidade","cheio","desgastado","assustar","assustador","carranca","carrancudo","infrutífero","frustrar","fugitivo","funeral","medo","sulco","fúria","espalhafato","fútil","forca","geriátrica","gueto","melancolia","sombrio","sorumbático","gonorréia","escornar","sangrento","grave","cinza","aflição","agravo","afligir","grave","severo","gemido","fundamentado","mal humorado","guilhotina","culpa","culpado","crédulo","desfigurado","cabresto","hesitante","jarretar","desvantagem","enforcamento","dificuldade","prejudicial","harry","odiar","detestável","ódio","assombrada","carro fúnebre","mágoa","sincero","sem coração","inferno","infernal","desamparado","desamparo","hemorragia","eremita","hediondo","dificultando","grisalho","engano","vagabundo","oco","holocausto","sem casa","com saudades de casa","homicídio","honesto","sem esperança","desespero","horrível","horrível","horripilante","horror","horrores","hospício","hospital","uivo","humilde","humilhado","mistificação","humilhar","humilhação","caçador","ferido","prejudicial","machucando","cabana","hidrocefalia","hino","idiotia","doente","ilegal","ilegítimo","doença","imoral","impossível","impotência","preso","prisão","imprudente","incapacidade","inadequado","inadequado","encarceramento","em caso","incesto","renda","incompatível","incompetente","sem importância","inconsiderado","inconveniente","incriminação","incurável","indiferença","indigente","ineficiência","ineficiente","ineptidão","desigualdade","indesculpável","infâmia","infanticídio","infeccioso","inferior","infertilidade","infidelidade","infligir","inflição","inibir","inóspito","desumano","desumanidade","hostil","ferir","ferido","prejudicial","prejuízo","insanidade","inseguro","insignificante","insolvência","insolvente","insulto","insultuoso","intransponível","entre","interceder","interessado","enterro","interrompeu","intervenção","intolerante","invadir","invasor","inválido","irreconciliável","irreparável","irritação","isolar","isolado","isolamento","cadeia","chocante","ciúme","jurisprudência","canil","raptar","matar","matança","dobre a finados","trabalhado","renda","atraso","lamento","lamentando","desmoronamento","lânguido","tarde","ação judicial","frouxo","sair","lepra","lésbica","letal","letargia","leucemia","mentira","sem vida","limitado","licor","apático","litigar","trancar","solitário","solidão","solitário","solitário","saudade","perder","a perderem","perda","perdido","adorável","diminuir","menor","humilde","loucura","luxuriante","linchar","louco","mal-estar","malária","malevolente","malicioso","malignidade","calandra","homicídio involuntário","margem","mártir","martírio","massacre","mausoléu","sem sentido","sarampo","manso","melancólico","melancolia","melodrama","fusão","memoriais","milícia","aborto espontâneo","miserável","miseravelmente","miséria","infortúnio","acidente","deturpação","ausência de","erro","mal-entendido","gemido","zombeteiro","molestação","monção","temperamental","mórbido","morbidez","morgue","moribundo","mortalidade","mortificação","mortuário","mãe","chorar","pesaroso","luto","caneca","assassinato","assassino","assassino","escuro","música","musical","mutilação","miopia","guardanapo","desagradável","nauseabundo","necessidade","nefasto","negativo","negligenciadas","negro","nepotismo","inferior","neuralgia","neurose","descumprimento","sem sentido","armadilha","nada","entorpecimento","nutritivo","obesidade","obit","obituário","obliterar","obliteração","desagradável","obstáculo","esquisitice","ofendido","ofensor","ofensa","mais velho","oneroso","ópera","ópio","oprimir","opressão","opressivo","opressor","orquestra","órfão","expulsar","explosão","exilado","ovação","atrasado","sobrecarga","superfaturada","sobrecarregado","que é devido","dor","triste","doloroso","dolorosamente","paralisia","pandemia","paralisia","paralisado","aparar","despedida","patético","escassez","indigente","penal","pena","penitência","pensativo","perdição","perigo","perigoso","perecer","pereceram","que perece","pernicioso","perpetrador","perplexidade","perseguição","perversão","pessimismo","pessimista","pinho","piedoso","armadilha","pena","praga","lamentoso","apelo","apuro","pilhagem","caça furtiva","pointless","veneno","envenenado","venenoso","polio","póstumo","pobreza","impotente","precário","sacerdócio","prisão","prisioneiro","provação","problema","procissão","progressão","processar","prostituição","psicose","soco","punido","punindo","punitivo","quieto","fanático","cremalheira","chuvoso","estupro","classificação","voraz","recessão","reincidência","refugiado","recusada","arrepender","lamentável","lamentava","Lamentando","rejeitar","rejeição","rejeita","recaída","relíquias","remisso","remorso","remover","reprimir","represália","censura","réquiem","ressentimento","demitir-se","demissão","resignado","resistindo","restringir","restrição","retardar","aposentadoria","retribuição","revogar","revolução","revólver","reumatismo","ridículo","roubar","roubo","romance","podridão","pedregulho","arruda","ruína","arruinado","ruinoso","rumor","fugir","ruptura","sabotar","sacrifícios","tristemente","tristeza","santificar","seiva","sarcasmo","sarcoma","sabor","cicatriz","escasso","mal","escassez","esquizofrenia","repreender","flagelo","scrapie","isolado","sedição","separar","senil","sem sentido","sentença","sepsia","seqüestro","seriedade","servil","revés","separação","cabana","manilha","vergonha","vergonhoso","quebrar","destruído","Concha","naufrágio","arrepio","escassez","tiro","guincho","encolher","mortalha","esquivar-se a","doente","repugnante","doentio","doença","pecado","pecaminoso","cantar","pecador","irmandade","derrapagem","chacina","matadouro","abate","escravo","escravidão","matador","lento","crise","estigma","desbaratar","bufar","soluço","socialista","soldado","sombrio","soneto","sórdido","dolorido","extremamente","dor","tristeza","triste","desumano","palmada","espectro","especulação","solteirona","splitting","Spoiler","entorse","borrasca","facada","estagnado","fome","roubar","estéril","sufocada","estigma","nascido morto","quietude","mesquinho","restrição","estrangular","maca","ferido","faixa","despojado","golpe","luta","submetido","subjugação","abaixamento","subverter","processar","sofredor","sofrimento","asfixiante","suicida","suicídio","taciturno","afundado","suprimir","cirurgia","rendição","rendição","amor","compreensivo","simpatizar","simpatia","síncope","mácula","embaçar","sarcasmo","imposto","choroso","provocação","tempestade","terminal","terminar","terminação","terrível","terrivelmente","formidável","terrorismo","terrorista","aterrorizar","roubo","teocrático","ladrao","sede","açoitar","debulhar","pulsar","tímido","tolerar","túmulo","tormento","tortura","suscetível","difícil","tragédia","traidor","vagabundo","lixo","traumático","caricatura","traição","tratar","tremor","tribulação","trapaça","tropeçar","tumor","tumulto","tirania","tirano","feiúra","úlcera","final","incapaz","inaceitável","inexplicável","não reconhecido","irrealizável","pouco atraente","insuportável","invicto","uncaring","mal pago","empresário","indesejável","indesejada","imortal","inquietação","ignorante","desempregado","desigual","inexplicável","Injusto","injustiça","desfavorável","imperdoável","infeliz","hostil","por cumprir","ímpio","infelicidade","infeliz","pouco saudável","sem importância","sem inspiração","desinteressado","desinteressante","sem convite","grosseiro","ilegal","azarado","não remunerado","desagradável","impopular","inédito","não correspondido","agitação","insatisfeito","destituir","sem êxito","sem título","indesejável","indisposto","sublevação","chateado","urna","em vão","desaparecido","varicela","vitela","vegetativo","vendeta","vítima","vitimados","violação","violência","violentamente","voto","vulgaridade","vulnerabilidade","waffle","lamúria","chafurdar","pálido","declínio","querendo","guerra","urdidura","desperdiçador","desperdiçando","fracamente","wearily","cansaço","cansado","ervas daninhas","chorar","choro","peso","choradeira","choramingar","viúva","viúvo","deserto","wildfire","intencional","fracote","estremecimento","vencedor","feitiçaria","retirar","murchar","aflição","mísero","lamentavelmente","usado","preocupado","preocupar-se","preocupante","pior","piora","sem valor","ferida","questiúnculas","naufrágio","naufragado","desgraçado","miserável","enrugada","transgressão","ilegal","erradamente");

        int f = 0, t = 0;

        StringTokenizer st = new StringTokenizer(log);
        int não = 0;
        while (st.hasMoreElements()) {
            //arranjar a parte do não

            String aux = st.nextToken();
            //System.out.println("Token: " + aux);
            if(aux.equals("não") || aux.equals("nao") || aux.equals("Nao") || aux.equals("Não")) não = 1;
            if (tri.contains(aux)) {
                if(não == 0) t++;
                //if(não == 1) f++;

                //return "triste";
            }
            if (fel.contains(aux)) {
                if(não == 0) f++;
                //if(não == 1) t++;
                //return "feliz";
            }
        }

        if(t != 0 || f != 0){
            //System.out.println("f: " + f + " t: " + t);
            if(t == f) return "neutral";
            if(t > f) return "triste";
            if(t < f) return "feliz";
        }

        // se deu mais de 5 backspaces está stressado:
        if (backspaces > 5 || log.equals(log.toUpperCase())) return "stressado";
        // se demorar + de 12 s está distraido:
        if (timeelapsed > 12000 || (timeelapsed>6000 && log.contains("ok"))) return "distraido";
        return "neutral";
    }
}
