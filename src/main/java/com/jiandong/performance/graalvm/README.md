All content are from
DaShaun - [Testing Your Way to Production Confidence with Native Images (Presentation)](https://dashaun.com/testing-native-images/#/)

## The Promise of Native Images

- Faster Startup
- Smaller Footprint
- Improved Performance

## Steps

1. Build Native Image

    - Refer
      Official [Gradle Packaging OCI Images](https://docs.spring.io/spring-boot/gradle-plugin/packaging-oci-image.html#build-image.examples)

    ```shell
    ./gradlew bootBuildImage --imageName=com.jiandong/spring-unknown-native-app:default
   
    docker images | grep spring-unknown-native-app
    ```

2. Run Native Image
    ```shell
    docker run -p 8080:8080 com.jiandong/spring-unknown-native-app:default
    ```

## The Challenge: Ensuring FEATURE Parity

- The performance will NOT match.
- Verify native image behaves like JVM application.
- Robust testing is essential.

## Integration Testing

- Validate with Integration Tests
- Use TestContainers
- During continuous integration

## Add Failsafe Plugin for integration tests