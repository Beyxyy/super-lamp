export interface IToast {
    id: number;
    message: string;
    type?: 'success'|'info'|'warning'|'error';
    duration?: number; // ms
}