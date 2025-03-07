# The various SSL stores and certificates were created with the following commands:
# Requires use of JDK 8+ keytool command.

# Clean up existing files
# -----------------------
rm -f *.crt *.csr *.keystore *.truststore *.pem *.pemcfg

# Create a key and self-signed certificate for the CA, to sign certificate requests and use for trust:
# ----------------------------------------------------------------------------------------------------
echo "Creating CA certificate for signing test keys"

keytool -storetype pkcs12 -keystore ca-pkcs12.keystore -storepass password -keypass password -alias ca -genkey -keyalg "RSA" -keysize 2048 -dname "O=My Trusted Inc.,CN=my-ca.org" -validity 9999 -ext bc:c=ca:true
keytool -storetype pkcs12 -keystore ca-pkcs12.keystore -storepass password -alias ca -exportcert -rfc > ca.crt

openssl pkcs12 -in ca-pkcs12.keystore -out ca-cert.pem -clcerts -nokeys -password pass:password

# Create a key pair for the broker, and sign it with the CA:
# ----------------------------------------------------------
echo
echo "Creating key pair for simulated broker and signing it with the CA"

keytool -storetype pkcs12 -keystore broker-pkcs12.keystore -storepass password -keypass password -alias broker -genkey -keyalg "RSA" -keysize 2048 -dname "O=Server,CN=localhost" -validity 9999 -ext bc=ca:false -ext eku=sA

keytool -storetype pkcs12 -keystore broker-pkcs12.keystore -storepass password -alias broker -certreq -file broker.csr
keytool -storetype pkcs12 -keystore ca-pkcs12.keystore -storepass password -alias ca -gencert -rfc -infile broker.csr -outfile broker.crt -validity 9999 -ext bc=ca:false -ext eku=sA

keytool -storetype pkcs12 -keystore broker-pkcs12.keystore -storepass password -keypass password -importcert -alias ca -file ca.crt -noprompt
keytool -storetype pkcs12 -keystore broker-pkcs12.keystore -storepass password -keypass password -importcert -alias broker -file broker.crt

openssl pkcs12 -in broker-pkcs12.keystore -out broker-cert.pem -clcerts -nokeys -password pass:password
openssl pkcs12 -in broker-pkcs12.keystore -out broker-key.pem -nocerts -nodes -password pass:password

cat broker-cert.pem ca-cert.pem > broker-cert-chain.pem

echo alias=broker >> broker-keystore.pemcfg
echo source.key=classpath:broker-key.pem >> broker-keystore.pemcfg
echo source.cert=classpath:broker-cert-chain.pem >> broker-keystore.pemcfg

# Create some alternative keystore types for testing:
# ---------------------------------------------------
echo
echo "Creating alternate key stores for simulated broker"

keytool -importkeystore -srckeystore broker-pkcs12.keystore -destkeystore broker-jceks.keystore -srcstoretype pkcs12 -deststoretype jceks -srcstorepass password -deststorepass password
keytool -importkeystore -srckeystore broker-pkcs12.keystore -destkeystore broker-jks.keystore -srcstoretype pkcs12 -deststoretype jks -srcstorepass password -deststorepass password

# Create a key pair for the broker with an unexpected hostname, and sign it with the CA:
# --------------------------------------------------------------------------------------
echo
echo "Creating key pair for simulated broker with wrong host and signing it with the CA"

keytool -storetype jks -keystore broker-wrong-host-jks.keystore -storepass password -keypass password -alias broker-wrong-host -genkey -keyalg "RSA" -keysize 2048 -dname "O=Server,CN=wronghost" -validity 9999 -ext bc=ca:false -ext eku=sA

keytool -storetype jks -keystore broker-wrong-host-jks.keystore -storepass password -alias broker-wrong-host -certreq -file broker-wrong-host.csr
keytool -storetype pkcs12 -keystore ca-pkcs12.keystore -storepass password -alias ca -gencert -rfc -infile broker-wrong-host.csr -outfile broker-wrong-host.crt -validity 9999 -ext bc=ca:false -ext eku=sA

keytool -storetype jks -keystore broker-wrong-host-jks.keystore -storepass password -keypass password -importcert -alias ca -file ca.crt -noprompt
keytool -storetype jks -keystore broker-wrong-host-jks.keystore -storepass password -keypass password -importcert -alias broker-wrong-host -file broker-wrong-host.crt

keytool -importkeystore -srckeystore broker-wrong-host-jks.keystore -destkeystore broker-wrong-host-pkcs12.keystore -srcstoretype jks -deststoretype pkcs12 -srcstorepass password -deststorepass password

openssl pkcs12 -in broker-wrong-host-pkcs12.keystore -out broker-wrong-host-cert.pem -clcerts -nokeys -password pass:password
openssl pkcs12 -in broker-wrong-host-pkcs12.keystore -out broker-wrong-host-key.pem -nocerts -nodes -password pass:password

cat broker-wrong-host-cert.pem ca-cert.pem > broker-wrong-host-cert-chain.pem

echo alias=broker-wrong-host > broker-wrong-host-keystore.pemcfg
echo source.key=classpath:broker-wrong-host-key.pem >> broker-wrong-host-keystore.pemcfg
echo source.cert=classpath:broker-wrong-host-cert-chain.pem >> broker-wrong-host-keystore.pemcfg

# Create trust stores for the broker, import the CA cert:
# -------------------------------------------------------
echo
echo "Creating trust store for simulated broker"

keytool -storetype pkcs12 -keystore broker-pkcs12.truststore -storepass password -keypass password -importcert -alias ca -file ca.crt -noprompt
keytool -importkeystore -srckeystore broker-pkcs12.truststore -destkeystore broker-jceks.truststore -srcstoretype pkcs12 -deststoretype jceks -srcstorepass password -deststorepass password
keytool -importkeystore -srckeystore broker-pkcs12.truststore -destkeystore broker-jks.truststore -srcstoretype pkcs12 -deststoretype jks -srcstorepass password -deststorepass password

openssl pkcs12 -in ca-pkcs12.keystore -out broker-truststore.pem -clcerts -nokeys -password pass:password

# Create a key pair for the client, and sign it with the CA:
# ----------------------------------------------------------
echo
echo "Creating key pair for Client and signing it with the CA"

keytool -storetype pkcs12 -keystore client-pkcs12.keystore -storepass password -keypass password -alias client -genkey -keyalg "RSA" -keysize 2048 -dname "O=Client,CN=client" -validity 9999 -ext bc=ca:false -ext eku=cA

keytool -storetype pkcs12 -keystore client-pkcs12.keystore -storepass password -alias client -certreq -file client.csr
keytool -storetype pkcs12 -keystore ca-pkcs12.keystore -storepass password -alias ca -gencert -rfc -infile client.csr -outfile client.crt -validity 9999 -ext bc=ca:false -ext eku=cA

keytool -storetype pkcs12 -keystore client-pkcs12.keystore -storepass password -keypass password -importcert -alias ca -file ca.crt -noprompt
keytool -storetype pkcs12 -keystore client-pkcs12.keystore -storepass password -keypass password -importcert -alias client -file client.crt

openssl pkcs12 -in client-pkcs12.keystore -out client-cert.pem -clcerts -nokeys -password pass:password
openssl pkcs12 -in client-pkcs12.keystore -out client-key.pem -nocerts -nodes -password pass:password

cat client-cert.pem ca-cert.pem > client-cert-chain.pem

echo alias=client > client-keystore.pemcfg
echo source.key=classpath:client-key.pem >> client-keystore.pemcfg
echo source.cert=classpath:client-cert-chain.pem >> client-keystore.pemcfg

# Create some alternative keystore types for testing:
# ---------------------------------------------------
echo
echo "Creating alternate key stores for client"

keytool -importkeystore -srckeystore client-pkcs12.keystore -destkeystore client-jceks.keystore -srcstoretype pkcs12 -deststoretype jceks -srcstorepass password -deststorepass password
keytool -importkeystore -srckeystore client-pkcs12.keystore -destkeystore client-jks.keystore -srcstoretype pkcs12 -deststoretype jks -srcstorepass password -deststorepass password

# Create a key pair for a second client, and sign it with the CA:
# ----------------------------------------------------------
echo
echo "Creating key pair for second Client and signing it with the CA"

keytool -storetype jks -keystore client2-jks.keystore -storepass password -keypass password -alias client2 -genkey -keyalg "RSA" -keysize 2048 -dname "O=Client2,CN=client2" -validity 9999 -ext bc=ca:false -ext eku=cA

keytool -storetype jks -keystore client2-jks.keystore -storepass password -alias client2 -certreq -file client2.csr
keytool -storetype pkcs12 -keystore ca-pkcs12.keystore -storepass password -alias ca -gencert -rfc -infile client2.csr -outfile client2.crt -validity 9999 -ext bc=ca:false -ext eku=cA

keytool -storetype jks -keystore client2-jks.keystore -storepass password -keypass password -importcert -alias ca -file ca.crt -noprompt
keytool -storetype jks -keystore client2-jks.keystore -storepass password -keypass password -importcert -alias client2 -file client2.crt

keytool -importkeystore -srckeystore client2-jks.keystore -destkeystore client2-pkcs12.keystore -srcstoretype jks -deststoretype pkcs12 -srcstorepass password -deststorepass password

openssl pkcs12 -in client2-pkcs12.keystore -out client2-cert.pem -clcerts -nokeys -password pass:password
openssl pkcs12 -in client2-pkcs12.keystore -out client2-key.pem -nocerts -nodes -password pass:password

cat client2-cert.pem ca-cert.pem > client2-cert-chain.pem

echo alias=client2 > client2-keystore.pemcfg
echo source.key=classpath:client2-key.pem >> client2-keystore.pemcfg
echo source.cert=classpath:client2-cert-chain.pem >> client2-keystore.pemcfg

# Create trust stores for the client, import the CA cert:
# -------------------------------------------------------
echo
echo "Creating trust store for Client"

keytool -storetype pkcs12 -keystore client-pkcs12.truststore -storepass password -keypass password -importcert -alias ca -file ca.crt -noprompt
keytool -importkeystore -srckeystore client-pkcs12.truststore -destkeystore client-jceks.truststore -srcstoretype pkcs12 -deststoretype jceks -srcstorepass password -deststorepass password
keytool -importkeystore -srckeystore client-pkcs12.truststore -destkeystore client-jks.truststore -srcstoretype pkcs12 -deststoretype jks -srcstorepass password -deststorepass password

openssl pkcs12 -in ca-pkcs12.keystore -out client-truststore.pem -clcerts -nokeys -password pass:password

# Create a truststore with self-signed certificate for an alternative CA, to
# allow 'failure to trust' of certs signed by the original CA above:
# ------------------------------------------------------------------
echo
echo "Creating alternate trust store for Client to test 'failure of trust'"

keytool -storetype jks -keystore other-ca-jks.truststore -storepass password -keypass password -alias other-ca -genkey -keyalg "RSA" -keysize 2048 -dname "O=Other Trusted Inc.,CN=other-ca.org" -validity 9999 -ext bc:c=ca:true
keytool -storetype jks -keystore other-ca-jks.truststore -storepass password -alias other-ca -exportcert -rfc > other-ca.crt
keytool -storetype jks -keystore other-ca-jks.truststore -storepass password -alias other-ca -delete
keytool -storetype jks -keystore other-ca-jks.truststore -storepass password -keypass password -importcert -alias other-ca -file other-ca.crt -noprompt

# Create a store with multiple key pairs for the client to allow for alias selection:
# ----------------------------------------------------------
echo
echo "Creating store with multiple key pairs for the client for alias selection"

keytool -importkeystore -srckeystore client-pkcs12.keystore -destkeystore client-multiple-keys-jks.keystore -srcstoretype pkcs12 -deststoretype jks -srcstorepass password -deststorepass password
keytool -storetype jks -keystore client-multiple-keys-jks.keystore -storepass password -alias ca -delete
keytool -importkeystore -srckeystore client2-jks.keystore -destkeystore client-multiple-keys-jks.keystore -srcstoretype jks -deststoretype jks -srcstorepass password -deststorepass password
