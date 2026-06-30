import { useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  IconButton,
  Paper,
  Snackbar,
  Stack,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tabs,
  Tooltip,
  Typography,
} from '@mui/material';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import {
  useDeleteTaskMutation,
  useGetTasksQuery,
} from '@/features/tasks/tasksApi';
import type { Task, TaskStatus } from '@/features/tasks/types';
import TaskFormDialog from '@/features/tasks/TaskFormDialog';

const STATUS_META: Record<TaskStatus, { label: string; color: 'default' | 'info' | 'success' }> = {
  TODO: { label: 'Yapılacak', color: 'default' },
  IN_PROGRESS: { label: 'Devam ediyor', color: 'info' },
  DONE: { label: 'Tamamlandı', color: 'success' },
};

type Filter = 'ALL' | TaskStatus;

export default function TasksPage() {
  const [filter, setFilter] = useState<Filter>('ALL');
  const statusArg = filter === 'ALL' ? undefined : { status: filter };
  const { data: tasks, isLoading, isFetching, isError, refetch } = useGetTasksQuery(statusArg);
  const [deleteTask, { isLoading: deleting }] = useDeleteTaskMutation();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editTarget, setEditTarget] = useState<Task | null>(null);
  const [snack, setSnack] = useState<string | null>(null);

  const openCreate = () => {
    setEditTarget(null);
    setDialogOpen(true);
  };
  const openEdit = (task: Task) => {
    setEditTarget(task);
    setDialogOpen(true);
  };

  const handleDelete = async (task: Task) => {
    if (!window.confirm(`"${task.title}" görevini silmek istediğinize emin misiniz?`)) return;
    try {
      await deleteTask(task.id).unwrap();
      setSnack('Görev silindi');
    } catch {
      setSnack('Silme başarısız — backend çalışıyor mu?');
    }
  };

  const counts = useMemo(() => {
    const c = { ALL: tasks?.length ?? 0, TODO: 0, IN_PROGRESS: 0, DONE: 0 } as Record<Filter, number>;
    (tasks ?? []).forEach((t) => (c[t.status] += 1));
    return c;
  }, [tasks]);

  return (
    <Stack spacing={3}>
      <Box>
        <Typography variant="h4" sx={{ mb: 1 }}>
          Canlı Demo · Görev Yönetimi API
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ maxWidth: 760 }}>
          Bu ekran, Spring Boot örnek uygulamasındaki <code>/api/tasks</code> REST API'sini
          tarayıcıdan kullanır. RTK Query ile her işlem (oluştur/güncelle/sil) sonrası liste
          otomatik tazelenir; doğrulama hataları backend'den gelen alan bazlı mesajlarla gösterilir.
        </Typography>
      </Box>

      <Paper variant="outlined" sx={{ borderRadius: 3 }}>
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          justifyContent="space-between"
          alignItems={{ xs: 'stretch', sm: 'center' }}
          sx={{ p: 1.5, gap: 1 }}
        >
          <Tabs
            value={filter}
            onChange={(_e, v) => setFilter(v)}
            variant="scrollable"
            scrollButtons="auto"
          >
            <Tab value="ALL" label={`Tümü (${counts.ALL})`} />
            <Tab value="TODO" label={`Yapılacak (${counts.TODO})`} />
            <Tab value="IN_PROGRESS" label={`Devam (${counts.IN_PROGRESS})`} />
            <Tab value="DONE" label={`Bitti (${counts.DONE})`} />
          </Tabs>
          <Stack direction="row" spacing={1}>
            <Tooltip title="Yenile">
              <span>
                <IconButton onClick={() => refetch()} disabled={isFetching}>
                  <RefreshRoundedIcon />
                </IconButton>
              </span>
            </Tooltip>
            <Button variant="contained" startIcon={<AddRoundedIcon />} onClick={openCreate}>
              Yeni Görev
            </Button>
          </Stack>
        </Stack>

        {isError && (
          <Alert severity="error" sx={{ m: 2 }}>
            Görevler yüklenemedi. Spring Boot backend'i <code>localhost:8080</code> üzerinde çalışıyor mu?
          </Alert>
        )}

        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell width={64}>#</TableCell>
                <TableCell>Başlık</TableCell>
                <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Açıklama</TableCell>
                <TableCell>Durum</TableCell>
                <TableCell align="center">Öncelik</TableCell>
                <TableCell align="right">İşlemler</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 6 }}>
                    <CircularProgress size={28} />
                  </TableCell>
                </TableRow>
              ) : (tasks ?? []).length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 6, color: 'text.secondary' }}>
                    Görev yok. “Yeni Görev” ile ekleyin.
                  </TableCell>
                </TableRow>
              ) : (
                (tasks ?? []).map((task) => {
                  const meta = STATUS_META[task.status];
                  return (
                    <TableRow key={task.id} hover>
                      <TableCell>{task.id}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{task.title}</TableCell>
                      <TableCell sx={{ display: { xs: 'none', md: 'table-cell' }, color: 'text.secondary' }}>
                        {task.description || '—'}
                      </TableCell>
                      <TableCell>
                        <Chip size="small" color={meta.color} label={meta.label} />
                      </TableCell>
                      <TableCell align="center">
                        <Chip size="small" variant="outlined" label={`P${task.priority}`} />
                      </TableCell>
                      <TableCell align="right">
                        <Tooltip title="Düzenle">
                          <IconButton size="small" onClick={() => openEdit(task)}>
                            <EditRoundedIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Sil">
                          <span>
                            <IconButton size="small" color="error" onClick={() => handleDelete(task)} disabled={deleting}>
                              <DeleteRoundedIcon fontSize="small" />
                            </IconButton>
                          </span>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  );
                })
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>

      <TaskFormDialog
        open={dialogOpen}
        task={editTarget}
        onClose={() => setDialogOpen(false)}
        onSuccess={(msg) => {
          setDialogOpen(false);
          setSnack(msg);
        }}
      />

      <Snackbar
        open={!!snack}
        autoHideDuration={3000}
        onClose={() => setSnack(null)}
        message={snack ?? ''}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      />
    </Stack>
  );
}
