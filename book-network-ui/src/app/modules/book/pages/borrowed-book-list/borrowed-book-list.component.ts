import { Component, OnInit } from '@angular/core';
import { BookResponse, BorrowedBookResponse, FeedbackRequest, PageResponseBorrowedBookResponse } from '../../../../services/models';
import { BookService, FeedbackService } from '../../../../services/services';

@Component({
  selector: 'app-borrowed-book-list',
  standalone: false,
  
  templateUrl: './borrowed-book-list.component.html',
  styleUrl: './borrowed-book-list.component.scss'
})
export class BorrowedBookListComponent implements OnInit{

  page = 0;
  size = 5;

  selectedBook: BorrowedBookResponse | undefined = undefined;

  borrowedBooks: PageResponseBorrowedBookResponse = {};

  feedbackRequest: FeedbackRequest = {
    bookId: 0,
    comment: '',
    note: 0
  };

  constructor(
    private bookService: BookService,
    private feedbackService: FeedbackService
  ){}

  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }

  returnBorrowedBook(book: BorrowedBookResponse){
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number;
    

  }

  findAllBorrowedBooks(){
    this.bookService.findAllBorrowedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp) => {
        this.borrowedBooks = resp;
      }
    })
  }


  goToFirstPage(){
    this.page = 0;
    this.findAllBorrowedBooks();
  }

  goToPreviousPage(){
    this.page--;
    this.findAllBorrowedBooks();

  }


  goToPage(page: number){
    this.page = page;
    this.findAllBorrowedBooks();

  }

  goToNextPage(){
    this.page++;
    this.findAllBorrowedBooks();

  }


  goToLastPage(){
    this.page == this.borrowedBooks.totalPage as number - 1;
    this.findAllBorrowedBooks();
  }


  get isLastPage(): boolean{
    return this.page == this.borrowedBooks.totalPage as number -1;
  }

  returnBook(withFeedBack: boolean){
    this.bookService.returnBorrowBook({
      'book-id': this.selectedBook ?.id as number
    }).subscribe({
      next: () => {
       if (withFeedBack) {
        this.giveFeedback();
       }
       this.selectedBook = undefined;
       this.findAllBorrowedBooks()
      }
    });
  }

  giveFeedback(){
    this.feedbackService.saveFeedback({
      body: this.feedbackRequest
    }).subscribe({
      next: () => {

      }
    });
  }
}