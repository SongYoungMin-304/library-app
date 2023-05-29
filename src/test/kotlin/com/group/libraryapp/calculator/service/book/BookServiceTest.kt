package com.group.libraryapp.calculator.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.service.book.BookService
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
    ){

    @AfterEach
    fun clean(){
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("책 등록이 정상 동작한다.")
    fun saveBookTest(){
        //given
        val bookRequest = BookRequest("이상한 나라의 엘리스");

        //when
        bookService.saveBook(bookRequest)

        //then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("이상한 나라의 엘리스")
    }

    @Test
    @DisplayName("책 대출이 정상 동작한다")
    fun loanBookTest(){
        //givenr
        bookRepository.save(Book("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("송영민", null))
        val bookLoanRequest = BookLoanRequest("송영민", "이상한 나라의 엘리스");

        //when
        bookService.loanBook(bookLoanRequest)

        //then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].bookName).isEqualTo("이상한 나라의 엘리스")

        assertThat(results[0].user.id).isEqualTo(savedUser.id)
        assertThat(results[0].isReturn).isFalse()

    }

    @Test
    @DisplayName("책이 진작 대출되어 있다면, 신규 대출이 실패한다.")
    fun loanBookFailTest(){
        //givenr
        bookRepository.save(Book("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("송영민", null))
        userLoanHistoryRepository.save(
            UserLoanHistory(
                savedUser,
                "이상한 나라의 엘리스",
                false
            )
        )
        val bookLoanRequest = BookLoanRequest("송영민", "이상한 나라의 엘리스");

        //when & then
        val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(bookLoanRequest)
        }.message

        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    @Test
    @DisplayName("책 반납이 정상 동작한다.")
    fun returnBookTest(){
        //given
        bookRepository.save(Book("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("송영민", null))
        userLoanHistoryRepository.save(
            UserLoanHistory(
                savedUser,
                "이상한 나라의 엘리스",
                false
            )
        )
        val bookLoanRequest = BookReturnRequest("송영민", "이상한 나라의 엘리스");

        //when
        bookService.returnBook(bookLoanRequest)

        //then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].isReturn).isTrue()
    }
}