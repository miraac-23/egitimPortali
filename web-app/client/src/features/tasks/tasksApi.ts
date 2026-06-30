import { baseApi } from '@/app/baseApi';
import type { Task, TaskRequest, TaskStatus } from './types';

// Canlı demo: Task CRUD endpoint'leri (/api/tasks).
// Mutasyonlardan sonra 'Task' tag'i invalidate edilir -> liste otomatik tazelenir.
export const tasksApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getTasks: builder.query<Task[], { status?: TaskStatus } | void>({
      query: (args) => {
        const status = args && args.status ? `?status=${args.status}` : '';
        return `/tasks${status}`;
      },
      providesTags: (result) =>
        result
          ? [...result.map((t) => ({ type: 'Task' as const, id: t.id })), { type: 'Task' as const, id: 'LIST' }]
          : [{ type: 'Task' as const, id: 'LIST' }],
    }),

    getTask: builder.query<Task, number>({
      query: (id) => `/tasks/${id}`,
      providesTags: (_r, _e, id) => [{ type: 'Task', id }],
    }),

    createTask: builder.mutation<Task, TaskRequest>({
      query: (body) => ({ url: '/tasks', method: 'POST', body }),
      invalidatesTags: [{ type: 'Task', id: 'LIST' }],
    }),

    updateTask: builder.mutation<Task, { id: number; body: TaskRequest }>({
      query: ({ id, body }) => ({ url: `/tasks/${id}`, method: 'PUT', body }),
      invalidatesTags: (_r, _e, arg) => [
        { type: 'Task', id: arg.id },
        { type: 'Task', id: 'LIST' },
      ],
    }),

    deleteTask: builder.mutation<void, number>({
      query: (id) => ({ url: `/tasks/${id}`, method: 'DELETE' }),
      invalidatesTags: [{ type: 'Task', id: 'LIST' }],
    }),
  }),
  overrideExisting: false,
});

export const {
  useGetTasksQuery,
  useGetTaskQuery,
  useCreateTaskMutation,
  useUpdateTaskMutation,
  useDeleteTaskMutation,
} = tasksApi;
