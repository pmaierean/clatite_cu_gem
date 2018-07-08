# Securing the website
## A bit on cryptography

The [Bouncy Castle](https://www.bouncycastle.org/java.html) is one of the most popular providers of cryptography. I am using it in this project too for the maturity of the library, experience working with it, and no restrictions (NSA or others)


## Encrypting data in the application

The administrator password is maintained in an encrypted file as seen in the [RepositoryUserResolver](https://github.com/pmaierean/clatite_cu_gem/blob/master/WebservicesParent/WebsiteParent/WebsiteContent/src/main/java/com/maiereni/host/web/jaxrs/service/impl/RepositoryUserResolverImpl.java)

To set up the encryption from scratch use [keytool](https://docs.oracle.com/cd/E19798-01/821-1841/gjrgy/index.html) to generate the private key, then convert the key store to the more generic type 'pkcs12'. The unit test class [BouncyCastleEncryptorImplTest]() demonstrates the work of the encryptor using the sample key store at resource/testKeystore.jks generated this way