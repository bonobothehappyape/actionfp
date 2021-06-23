import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IAuditRecomm, defaultValue } from 'app/shared/model/audit-recomm.model';

export const ACTION_TYPES = {
  SEARCH_AUDITRECOMMS: 'auditRecomm/SEARCH_AUDITRECOMMS',
  FETCH_AUDITRECOMM_LIST: 'auditRecomm/FETCH_AUDITRECOMM_LIST',
  FETCH_AUDITRECOMM: 'auditRecomm/FETCH_AUDITRECOMM',
  CREATE_AUDITRECOMM: 'auditRecomm/CREATE_AUDITRECOMM',
  UPDATE_AUDITRECOMM: 'auditRecomm/UPDATE_AUDITRECOMM',
  PARTIAL_UPDATE_AUDITRECOMM: 'auditRecomm/PARTIAL_UPDATE_AUDITRECOMM',
  DELETE_AUDITRECOMM: 'auditRecomm/DELETE_AUDITRECOMM',
  RESET: 'auditRecomm/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAuditRecomm>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type AuditRecommState = Readonly<typeof initialState>;

// Reducer

export default (state: AuditRecommState = initialState, action): AuditRecommState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_AUDITRECOMMS):
    case REQUEST(ACTION_TYPES.FETCH_AUDITRECOMM_LIST):
    case REQUEST(ACTION_TYPES.FETCH_AUDITRECOMM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_AUDITRECOMM):
    case REQUEST(ACTION_TYPES.UPDATE_AUDITRECOMM):
    case REQUEST(ACTION_TYPES.DELETE_AUDITRECOMM):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_AUDITRECOMM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_AUDITRECOMMS):
    case FAILURE(ACTION_TYPES.FETCH_AUDITRECOMM_LIST):
    case FAILURE(ACTION_TYPES.FETCH_AUDITRECOMM):
    case FAILURE(ACTION_TYPES.CREATE_AUDITRECOMM):
    case FAILURE(ACTION_TYPES.UPDATE_AUDITRECOMM):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_AUDITRECOMM):
    case FAILURE(ACTION_TYPES.DELETE_AUDITRECOMM):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_AUDITRECOMMS):
    case SUCCESS(ACTION_TYPES.FETCH_AUDITRECOMM_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_AUDITRECOMM):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_AUDITRECOMM):
    case SUCCESS(ACTION_TYPES.UPDATE_AUDITRECOMM):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_AUDITRECOMM):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_AUDITRECOMM):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/audit-recomms';
const apiSearchUrl = 'api/_search/audit-recomms';

// Actions

export const getSearchEntities: ICrudSearchAction<IAuditRecomm> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_AUDITRECOMMS,
  payload: axios.get<IAuditRecomm>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IAuditRecomm> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_AUDITRECOMM_LIST,
  payload: axios.get<IAuditRecomm>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IAuditRecomm> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_AUDITRECOMM,
    payload: axios.get<IAuditRecomm>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IAuditRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_AUDITRECOMM,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAuditRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_AUDITRECOMM,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IAuditRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_AUDITRECOMM,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAuditRecomm> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_AUDITRECOMM,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
