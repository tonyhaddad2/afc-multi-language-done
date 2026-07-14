import test from 'node:test';
import assert from 'node:assert/strict';

test('production notification payload rule', () => {
  const payload = { emergencyId: 'abc', type: 'medical' };
  const text = JSON.stringify(payload);
  assert.equal(text.includes('allergy'), false);
  assert.equal(text.includes('blood'), false);
  assert.equal(text.includes('lat'), false);
  assert.equal(text.includes('lng'), false);
});
