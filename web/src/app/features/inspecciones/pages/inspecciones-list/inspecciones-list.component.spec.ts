import { describe, expect, it } from 'vitest';

type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
};

const buildPage = <T>(items: T[], page: number, size: number): PageResponse<T> => {
  const totalElements = items.length;
  const totalPages = Math.max(1, Math.ceil(totalElements / size));
  const start = page * size;
  const content = items.slice(start, start + size);

  return {
    content,
    totalElements,
    totalPages,
    size,
    number: page,
    first: page === 0,
    last: page >= totalPages - 1
  };
};

describe('Inspecciones pagination contract', () => {
  it('uses 8 items per page and exposes metadata', () => {
    const allItems = Array.from({ length: 20 }, (_, index) => ({ id: index + 1, tema: `Inspección ${index + 1}` }));
    const page = buildPage(allItems, 0, 8);

    expect(page.content.length).toBe(8);
    expect(page.size).toBe(8);
    expect(page.totalPages).toBe(3);
    expect(page.totalElements).toBe(20);
    expect(page.first).toBe(true);
  });

  it('moves to the next page with the correct slice', () => {
    const allItems = Array.from({ length: 20 }, (_, index) => ({ id: index + 1, tema: `Inspección ${index + 1}` }));
    const page = buildPage(allItems, 1, 8);

    expect(page.number).toBe(1);
    expect(page.content[0].id).toBe(9);
    expect(page.content.length).toBe(8);
    expect(page.first).toBe(false);
  });
});
