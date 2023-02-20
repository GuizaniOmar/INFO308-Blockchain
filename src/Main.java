

public class Main {
    public static void main(String[] args) {

            try{
                String ClefPublic = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt5mo5LrPLpe36HIOGYNu+zO98TcsFwtI+AqiIHHV6R2D1n97PyOcgdwc7QroH7fW5szdXjzTEpiJRLJ2/zXMAhBv0o79XOHoP8cdjUp1hdkaOXmQEI8Zzawsbyd0pZhBLmayUodwkZMs7ie0JRDnEpTMcAcz1gFTmTjrcCE79BINXg+W1uuQ6QdEnyADDPnOf2DofuKh40KBHJkoSAwC31L+Xo5L5631+wEcR5Hz1LLN9KUDGUrD2AVfo+r/Y/UAUSQ2n7m4NMVjNaed5K6zMznZbRtadI0w0e12UBx2zBa7UQaapqWu1xmuiHd8BzbJyXwYU8283FlfU3igmtgJWQIDAQAB";
                String ClefPrivee = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC3majkus8ul7focg4Zg277M73xNywXC0j4CqIgcdXpHYPWf3s/I5yB3BztCugft9bmzN1ePNMSmIlEsnb/NcwCEG/Sjv1c4eg/xx2NSnWF2Ro5eZAQjxnNrCxvJ3SlmEEuZrJSh3CRkyzuJ7QlEOcSlMxwBzPWAVOZOOtwITv0Eg1eD5bW65DpB0SfIAMM+c5/YOh+4qHjQoEcmShIDALfUv5ejkvnrfX7ARxHkfPUss30pQMZSsPYBV+j6v9j9QBRJDafubg0xWM1p53krrMzOdltG1p0jTDR7XZQHHbMFrtRBpqmpa7XGa6Id3wHNsnJfBhTzbzcWV9TeKCa2AlZAgMBAAECggEAElMOzU1r0j2oSFXh3GAG2qGOap3UE7S3orIE/nH+JJciim0M0v9t5nQemvYEtu21UDHQQlCVzHxW7vlU8SHga7BSSNSmqveNI5hu9aQh2KsTd/sPCugNbj6p7KomdUSd9NChKaWE8bKbwGVMEIsAVRVu1huR6+EUA+3vabCXeEONmgrvdnOoStQGrWcREskivtJOeZKLeBB53aAEDVCKDeTP3cNxcSikq5B+wMqXIkljEkjnwDKzkXU10KKZJDG+pjHoO6hyWBhn3qee2sLz9u/7OKCFOnHPSu9xpnoaELdPEneY64pDdmBR6rtKL9Q6lAJmHuan4p60Vi78DWTYAQKBgQDKIRbwZ78N2YhhgRWqLdztX95HvAW2YpAQQbdcPU4eLvDT1JM/2cVNTc9Kz8H/0Ngx7er9JHZUhvO39WFEstYCoOJV6BRKlNrE7T1y5Hw4sFI0HGXdaOVoFImL/Rb04KetLd2diqaGR6N0DjnBlZVHi05w7LfkzA3vuiLYI6EVAQKBgQDoiF5jdFXZ92nZtR9EaIRqxa/mFDDAWV2hz/ecAcuTvDVS3hNX9T+rNl2t4W6AzGAYJRk0cq3aqk/Libt7ag0meO8VsBDmf/1n4Qhhx1Zq8FYMETLQLOvG1QArX9RKRd3Sxi6H9vcYkTFBmTrp38mR439AQ1J6C0MP6Vv1JWu8WQKBgF+OARApm5JqkiS2J43KHLVDKwvygSjVs5Fb39kFSlbOjh82UV5QDwwairKtQOM00d/Yv8xoXmBbZABFSnR1ruKTOCywiFcxw7JxDSmxhmAuSs5D1owzOLBZdSTuwtmFEv+1vRzrHQpB9623w+oWUvn9i1mrLsxFAxmffzV6sn4BAoGAGrItQ/XDNXb2LAxjPpNRQIDZpOyEfFDGMyGRJ9P870UYSh880UhSuvFO5/uNmDPehGcd8auI0iXja1aws4aFY/lWWYMRLaVcDmUDdVZRUY2uE0yWLNg7aWRi1Jf3418KDHy8Mtfjnmps4T8aSGds2NbpcRNJkMFiPZ1o9UgKimECgYAT4+iFronT0XHC/5FXNMtzheaa+bG971DCeCezrg2ptI7p/AhEpP35zzaH65DJ4cgBGCasr7xyiu1c3rwB/G0YnMC6m8+zx4EB7LgAFLrG2zH5U89fxpqa9UaEFtW+EM4kZjj7G5G7/LdbGRxov3DtFHLocDDLwoeHWrGGOuYifQ==";
                // Ici on utilise la clef déjà existante
                RSA rsa = new RSA(ClefPrivee,ClefPublic);
                SHA2 sha2 = new SHA2();
                String MessageSha2 = sha2.encrypt("Bonjours");
                System.out.println("On va encrypter en SHA2: " + MessageSha2);


              // Ici on génère une clef ! RSA rsa = new RSA();

                String message = "Bonjour";
                System.out.println("Message à encoder: " + message);

                String encoded = rsa.encode(message, rsa.privateKey);
                System.out.println("Message encodé: " + encoded);

                String decoded = rsa.decode(encoded, rsa.publicKey);
                System.out.println("Message décodé: " + decoded);

                String publicKey = rsa.publicKeyToString();
                System.out.println("Clef Public: " + publicKey);
                String privateKey = rsa.privateKeyToString();
                System.out.println("Clef privée: " + privateKey);

            }catch(Exception e){
            System.out.println("Exception produite !");
        }




    }
}