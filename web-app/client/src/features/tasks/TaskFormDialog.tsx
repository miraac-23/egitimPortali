import { useEffect, useState } from 'react';
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Stack,
  TextField,
} from '@mui/material';
import { useCreateTaskMutation, useUpdateTaskMutation } from './tasksApi';
import type { ApiErrorBody, Task, TaskRequest, TaskStatus } from './types';

interface Props {
  open: boolean;
  task: Task | null; // null -> oluşturma, dolu -> düzenleme
  onClose: () => void;
  onSuccess: (message: string) => void;
}

const STATUS_OPTIONS: { value: TaskStatus; label: string }[] = [
  { value: 'TODO', label: 'Yapılacak' },
  { value: 'IN_PROGRESS', label: 'Devam ediyor' },
  { value: 'DONE', label: 'Tamamlandı' },
];

const EMPTY: TaskRequest = { title: '', description: '', status: 'TODO', priority: 3 };

export default function TaskFormDialog({ open, task, onClose, onSuccess }: Props) {
  const isEdit = task !== null;
  const [form, setForm] = useState<TaskRequest>(EMPTY);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);

  const [createTask, { isLoading: creating }] = useCreateTaskMutation();
  const [updateTask, { isLoading: updating }] = useUpdateTaskMutation();
  const saving = creating || updating;

  // Diyalog açıldığında formu doldur/sıfırla.
  useEffect(() => {
    if (open) {
      setForm(task ? { title: task.title, description: task.description ?? '', status: task.status, priority: task.priority } : EMPTY);
      setFieldErrors({});
      setFormError(null);
    }
  }, [open, task]);

  const update = <K extends keyof TaskRequest>(key: K, value: TaskRequest[K]) => {
    setForm((f) => ({ ...f, [key]: value }));
    setFieldErrors((e) => {
      const { [key as string]: _omit, ...rest } = e;
      return rest;
    });
  };

  const handleSubmit = async () => {
    setFormError(null);
    setFieldErrors({});
    const body: TaskRequest = { ...form, description: form.description?.trim() || undefined };
    try {
      if (isEdit && task) {
        await updateTask({ id: task.id, body }).unwrap();
        onSuccess('Görev güncellendi');
      } else {
        await createTask(body).unwrap();
        onSuccess('Görev oluşturuldu');
      }
    } catch (err) {
      // RTK Query hata zarfı: { status, data }
      const data = (err as { data?: ApiErrorBody }).data;
      if (data?.fieldErrors) {
        setFieldErrors(data.fieldErrors);
        setFormError(data.message ?? 'Doğrulama hatası');
      } else {
        setFormError('İşlem başarısız. Backend çalışıyor mu?');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>{isEdit ? 'Görevi Düzenle' : 'Yeni Görev'}</DialogTitle>
      <DialogContent>
        <Stack spacing={2.5} sx={{ mt: 1 }}>
          {formError && <Alert severity="error">{formError}</Alert>}
          <TextField
            label="Başlık"
            value={form.title}
            onChange={(e) => update('title', e.target.value)}
            error={!!fieldErrors.title}
            helperText={fieldErrors.title}
            fullWidth
            autoFocus
            required
          />
          <TextField
            label="Açıklama"
            value={form.description}
            onChange={(e) => update('description', e.target.value)}
            error={!!fieldErrors.description}
            helperText={fieldErrors.description}
            fullWidth
            multiline
            minRows={2}
          />
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField
              select
              label="Durum"
              value={form.status}
              onChange={(e) => update('status', e.target.value as TaskStatus)}
              error={!!fieldErrors.status}
              helperText={fieldErrors.status}
              fullWidth
            >
              {STATUS_OPTIONS.map((o) => (
                <MenuItem key={o.value} value={o.value}>
                  {o.label}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              type="number"
              label="Öncelik (1-5)"
              value={form.priority}
              onChange={(e) => update('priority', Number(e.target.value))}
              error={!!fieldErrors.priority}
              helperText={fieldErrors.priority ?? '1 = düşük, 5 = yüksek'}
              inputProps={{ min: 1, max: 5 }}
              fullWidth
            />
          </Stack>
        </Stack>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={saving}>
          Vazgeç
        </Button>
        <Button variant="contained" onClick={handleSubmit} disabled={saving}>
          {saving ? 'Kaydediliyor…' : isEdit ? 'Güncelle' : 'Oluştur'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
