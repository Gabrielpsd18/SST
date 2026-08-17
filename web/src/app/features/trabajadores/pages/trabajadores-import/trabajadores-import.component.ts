import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImportacionService } from '../../services/importacion.service';

@Component({
  selector: 'app-trabajadores-import',
  standalone: true,
  imports:[CommonModule, FormsModule],
  templateUrl: './trabajadores-import.component.html',
  styleUrl: './trabajadores-import.component.scss'
})
export class TrabajadoresImportComponent implements OnInit {
  private readonly importService = inject(ImportacionService);

  protected selectedFile: File | null = null;
  protected monthOption: 'THIS'|'NEXT'|'PREV' = 'THIS';
  

  protected preview: any = null;
  protected batchId: number | null = null;
  protected issues: any[] = [];
  protected loading = false;

  ngOnInit(): void {}

  onFileSelected(event: Event){
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0){
      this.selectedFile = input.files[0];
    }
  }

  upload(){
    if (!this.selectedFile) return;
    this.loading = true;
    this.importService.upload(this.selectedFile, this.monthOption).subscribe({
      next: (res)=>{
        this.preview = res;
        this.batchId = res.batchId;
        this.loading = false;
        this.loadIssues();
      },
      error: (err)=>{
        console.error(err);
        this.loading = false;
      }
    });
  }

  apply(){
    if (!this.batchId) return;
    this.loading = true;
    this.importService.apply(this.batchId).subscribe({
      next: (res)=>{
        this.preview = res;
        this.loading = false;
        this.loadIssues();
      },
      error: (err)=>{
        console.error(err);
        this.loading = false;
      }
    });
  }

  loadIssues(){
    if (!this.batchId) return;
    this.importService.listIssues(this.batchId).subscribe({
      next: (res)=>{ this.issues = res || []; },
      error: (err)=>{ console.error(err); }
    });
  }

  resolve(issue: any, action: string = 'CREATE_MISSING'){
    if (!this.batchId) return;
    this.importService.resolveIssue(this.batchId, issue.id, action).subscribe({
      next: ()=>{ this.loadIssues(); },
      error: (err)=>{ console.error(err); }
    });
  }
}
