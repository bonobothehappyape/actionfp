import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IAuditSubRecomm, defaultValue } from 'app/shared/model/audit-sub-recomm.model';

export const ACTION_TYPES = {
  SEARCH_AUDITSUBRECOMMS: 'auditSubRecomm/SEARCH_AUDITSUBRECOMMS',
  FETCH_AUDITSUBRECOMM_LIST: 'auditSubRecomm/FETCH_AUDITSUBRECOMM_LIST',
  FETCH_AUDITSUBRECOMM: 'auditSubRecomm/FETCH_AUDITSUBRECOMM',
  CREATE_AUDITSUBRECOMM: 'auditSubRecomm/CREATE_AUDITSUBRECOMM',
  UPDATE_AUDITSUBRECOMM: 'auditSubRecomm/UPDATE_AUDITSUBRECOMM',
  PARTIAL_UPDATE_AUDITSUBRECOMM: 'auditSubRecomm/PARTIAL_UPDATE_AUDITSUBRECOMM',
  DELETE_AUDITSUBRECOMM: 'auditSubRecomm/DELETE_AUDITSUBRECOMM',
  RESET: 'auditSubRecomm/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAuditSubRecomm>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type AuditSubRecommState = Readonly<typeof initialState>;

// Reducer

export default (state: AuditSubRecommState = initialState, action): AuditSubRecommState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_AUDITSUBRECOMMS):
    case REQUEST(ACTION_TYPES.FETCH_AUDITSUBRECOMM_LIST):
    case REQUEST(ACTION_TYPES.FETCH_AUDITSUBRECOMM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_AUDITSUBRECOMM):
    case REQUEST(ACTION_TYPES.UPDATE_AUDITSUBRECOMM):
    case REQUEST(ACTION_TYPES.DELETE_AUDITSUBRECOMM):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_AUDITSUBRECOMM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_AUDITSUBRECOMMS):
    case FAILURE(ACTION_TYPES.FETCH_AUDITSUBRECOMM_LIST):
    case FAILURE(ACTION_TYPES.FETCH_AUDITSUBRECOMM):
    case FAILURE(ACTION_TYPES.CREATE_AUDITSUBRECOMM):
    case FAILURE(ACTION_TYPES.UPDATE_AUDITSUBRECOMM):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_AUDITSUBRECOMM):
    case FAILURE(ACTION_TYPES.DELETE_AUDITSUBRECOMM):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_AUDITSUBRECOMMS):
    case SUCCESS(ACTION_TYPES.FETCH_AUDITSUBRECOMM_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_AUDITSUBRECOMM):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_AUDITSUBRECOMM):
    case SUCCESS(ACTION_TYPES.UPDATE_AUDITSUBRECOMM):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_AUDITSUBRECOMM):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_AUDITSUBRECOMM):
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

const apiUrl = 'api/audit-sub-recomms';
const apiSearchUrl = 'api/_search/audit-sub-recomms';

// Actions

export const getSearchEntities: ICrudSearchAction<IAuditSubRecomm> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_AUDITSUBRECOMMS,
  payload: axios.get<IAuditSubRecomm>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IAuditSubRecomm> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_AUDITSUBRECOMM_LIST,
  payload: axios.get<IAuditSubRecomm>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IAuditSubRecomm> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_AUDITSUBRECOMM,
    payload: axios.get<IAuditSubRecomm>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IAuditSubRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_AUDITSUBRECOMM,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAuditSubRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_AUDITSUBRECOMM,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IAuditSubRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_AUDITSUBRECOMM,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAuditSubRecomm> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_AUDITSUBRECOMM,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
