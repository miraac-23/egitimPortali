import { baseApi } from '@/app/baseApi';
import type { Category, TopicDetail, TopicSummary } from './types';

// Eğitim içeriği endpoint'leri (GET /api/content/...).
export const contentApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getCategories: builder.query<Category[], void>({
      query: () => '/content/categories',
      providesTags: ['Category'],
    }),

    getTopics: builder.query<TopicSummary[], { category?: string; q?: string } | void>({
      query: (args) => {
        const params = new URLSearchParams();
        if (args && args.category) params.set('category', args.category);
        if (args && args.q) params.set('q', args.q);
        const qs = params.toString();
        return `/content/topics${qs ? `?${qs}` : ''}`;
      },
      providesTags: ['Topic'],
    }),

    getTopic: builder.query<TopicDetail, { category: string; slug: string }>({
      query: ({ category, slug }) => `/content/topics/${category}/${slug}`,
      providesTags: (_result, _err, arg) => [{ type: 'Topic', id: `${arg.category}/${arg.slug}` }],
    }),
  }),
  overrideExisting: false,
});

export const { useGetCategoriesQuery, useGetTopicsQuery, useGetTopicQuery } = contentApi;
