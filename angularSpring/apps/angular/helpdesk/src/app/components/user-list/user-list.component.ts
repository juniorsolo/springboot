import { Component, OnInit } from '@angular/core';
import { SharedService } from 'src/app/services/shared.service';
import { DialogService} from '../../dialog.service';
  import { from } from 'rxjs';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  page: number = 0;
  count: number = 5;
  pages: Array<number>;
  shared : SharedService;
  message: {};
  classCss: {};
  listUser=[];

  constructor(private dialogService : DialogService) { }

  ngOnInit() {
  }

}
