# library-app

# 실전! 코틀린과 스프링 부트로 도서관리 애플리케이션 개발하기 (Java 프로젝트 리팩토링)

## Section1

**계산기 코드**

```kotlin
class Calculoator(
  private var number: Int,
) {
 
 fun add(operand: Int){
   this.number += operand
 }

 fun minus(operand: Int){
   this.number -= operand
 }

 fun multiply(operand: Int){
   this.number *= operand
 }

 fun divide(operand: Int){
   if(operand == 0){
     throw IllegalArugmentException("0으로 나눌 수 없습니다.")
   }
   this.number /= operand
 }
}
```

**테스트 코드**

```kotlin
class CalculatorTest{

  fun addTest(){
    val calculator = Calculator(5)
    calculator.add(3)
  }
}
```

- class를 data class로 변경해서 확인

```kotlin
data class Calculator(
  private var number: Int,
){
 // 생략...
}

fun addTest() {
   val calculator = Calculator(5)
   calculator.add(3)
val expectedResult = Calculator(8)

if (calculator != expectedResult) {
   throw IllegalStateException()
}
```

**메인함수**

```kotlin
fun main() {
   val calculatorTest = CalculatorTest()
   calculatorTest.addTest()
}
```

※ 테스트 메소드 given - when - then 패턴

```kotlin
fun addTest(){

  // given 
  val calculator = Calculator(5)

  // when
  calculator.add(3)

  // then 
  if(calculator.number != 8){
    throw IllegalStateException()
  }
}
```

### Junit  리팩토링

※ junit 특징

@Test : 테스트 메소드를 지정한다. 테스트 메소드를 실행하는 과정에서 오류가 없으면 성공

@BeforeEach : 각 테스트 메소드가 수행되기 전에 실행되는 메소드를 지정한다.

@AfterEach : 각 테스트가 수행된 후에 실행되는 메소드를 지정한다.

@BeforeAll : 모든 테스트를 수행하기 전에 최초 1회 수행되는 메소드를 지정한다.

- 코틀린에서는 @JvmStatic 을 붙여 주어야 한다.

@AfterAll: 모든 테스트를 수행한 후 최후 1회 수행되는 메소드를 지정한다.

- 코틀린에서는 @JvmStatic을 붙여 주어야 한다.

![image](https://github.com/SongYoungMin-304/library-app/assets/56577599/ce657e12-76fe-44e7-9a30-50efee29f0b5)


```kotlin
val isNew = true

assertThat(isNew).isTrue
assertThat(isNew).isFalse

val people = listOf(Person("A"), Persion("B"))
assertThat(people).hasSize(2)

assertThat(people).extracting("name").containsExactlyInAnyOrder("A","B")

// 순서 중요
assertThat(people).extracting("name").containsExactly("A", "B")

// function1() 을 실행했을때 예외가 나오는 지 검증한다.
assertThrows<IllegalArugmentException>{
  function1()
}

// 예외 메시지까지 검증할 수 있다.
val message = assertThrows<IllegalArugumentException> {
  function1()
}.message
assertThat(message).isEqualTo("잘못된 값이 들어왔습니다.")
```

```kotlin
class CalculatorTest2V2 {

 @Test
 fun addTest() {
  // given
  val calculator = Calculator(5)

  // when
  calculator.add(3)

  // then 
  assertThat(calculaotr.umber).isEqualTo(8)
}

@Test
fun minusTest() {
  // given
  val calculator = Calculator(5)

  // when
  calculator.minus(3)

  // then
  assertThat(calculator.number).isEqualTo(2)

}

@Test
fun multiplyTest(){
 // given
 val calculator = Calculator(5)

 // when
 calculator.multiply(3)

 // then
 assertThat(calculator.number).isEqualTo(15)
}

@Test
fun divideExceptionTest(){
 // given
 val calculator = Calculator(5)
 
 // when & then
 val message = assertThrows<IllegalArugmentException>{
   calculator.divide(0)
 }.message
 assertThan(message).isEqualTo("0으로 나눌 수 없습니다.")

 }

```

![image](https://github.com/SongYoungMin-304/library-app/assets/56577599/6b24e99f-9b6d-40ba-9486-2069e7272931)

![image](https://github.com/SongYoungMin-304/library-app/assets/56577599/bece1745-5a85-4e4a-9666-d797e72be8c4)

![image](https://github.com/SongYoungMin-304/library-app/assets/56577599/6d43221c-8a20-4722-af04-db34e90d8850)

![image](https://github.com/SongYoungMin-304/library-app/assets/56577599/95451c3c-1e04-482e-aff5-124367ba0bfa)


### 유저 관련 테스트

```kotlin
@SprinbBootTest
class UserServiceTest @Autowired constructor(
  private val userService: UserService,
  private val userRepository: UserRepository,
){

 @AfterEach
 fun clean() {
   userRepository.deleteAll()
 }

 @Test
 fun saveUserTest() {
  // given
  val request = UserCreateRequest("최태현", null)

  // when 
  userService.saveUser(request)

  // then
  val users = userRepository.findAll()
  assertThat(users).hasSize(1)
 }

  @Test
  fun getUsersTest() {

  // given
  userRepository.saveAll(listOf(
  User("A", 20),
  User("B", null),
  ))
  
  // when
  val results = userService.getUsers()
  
  // then
  assertThat(results).hasSize(2)
  assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B")
  assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
 }

  @Test
  fun updateUserNameTest() {
   // given
   val savedUser = userRepository.save(User("A", null))
   val request = UserUpdateRequest(savedUser.id!!, "B")

   // when
   userService.updateUserName(request)
   val result = userRepository.findAll()[0]
   assertThat(result.name).isEqualTo("B")
}

   @Test
   @DisplayName("유저 삭제가 정상 동작한다.")
   fun deleteUserTest() { 
   섹션 1. 도서관리 애플리케이션 리팩토링 준비하기 33
  
   // given
   userRepository.save(User("A", null))

   // when
   userService.deleteUser("A")

   // then
   assertThat(userRepository.findAll()).isEmpty()
}

}
```

- SpringBootTest : 스프링 컨텍스트를 띄우는 테스트 임을 표시한다. 이 테스트가 실행될 때는 컨텍스트가 자동으로 뜨게 된다.
- Autowired : 생성자를 통해 Bean을 주입받기 위한 어노테이션