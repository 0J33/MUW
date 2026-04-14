import { readFileSync, existsSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { parseAbilities, parseChampions, type AbilityTemplate, type ChampionTemplate } from '@muw/shared';

// CSV lookup order:
//   1. $MUW_DATA_DIR (override — used in prod where the bundle sits flat)
//   2. sibling `data/` dir next to the running module (bundled deploy layout)
//   3. ../../shared/data from the server/src/ source tree (dev monorepo layout)
const here = dirname(fileURLToPath(import.meta.url));
const DATA_DIR = resolveDataDir();

function resolveDataDir(): string {
  if (process.env.MUW_DATA_DIR) return process.env.MUW_DATA_DIR;
  const siblingData = join(here, 'data');
  if (existsSync(join(siblingData, 'Champions.csv'))) return siblingData;
  const monorepoData = join(here, '..', '..', 'shared', 'data');
  if (existsSync(join(monorepoData, 'Champions.csv'))) return monorepoData;
  return siblingData; // fall through, will throw on read with a meaningful path
}

export const abilityTemplates: AbilityTemplate[] = parseAbilities(
  readFileSync(join(DATA_DIR, 'Abilities.csv'), 'utf8'),
);
export const championTemplates: ChampionTemplate[] = parseChampions(
  readFileSync(join(DATA_DIR, 'Champions.csv'), 'utf8'),
);

export function championByName(name: string): ChampionTemplate | undefined {
  return championTemplates.find(c => c.name === name);
}
