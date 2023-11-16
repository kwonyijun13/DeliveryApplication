KAKAO LOGIN
- https://developers.kakao.com/docs/latest/en/kakaologin/android
- *Maven not added

KEY HASHES
- Key hashes are the values hashed from the certificate fingerprints of the certificate
- and used to check if the app is malicious and the updates or changes are from the original app.

FIREBASE (https://firebase.google.com/docs/auth/android/password-auth?authuser=0#java_1)
1. https://console.firebase.google.com/?pli=1
2. Add app
3. Obtain SHA-1 Fingerprint (Secure Hash Algorithm 1)
   1. Open 'Gradle' on the right
   2. Click the first button under the word 'Gradle'
   3. Search 'signingReport' and hit enter
   4. Now it appears in console
   5. Remember to remove after
4. Register App
5. Download google-services.json 
6. Add dependencies
7. Add plugin
