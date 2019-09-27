import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Ticket } from '../model/ticket.model';
import { HELP_DESK_API} from './helpdesk.api';

@Injectable({
  providedIn: 'root'
})

export class TicketService {

  constructor(private http:HttpClient) { }

  createOrUpdate(ticket: Ticket){
    
  }
}
