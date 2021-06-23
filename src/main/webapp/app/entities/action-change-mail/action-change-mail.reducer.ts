import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IActionChangeMail, defaultValue } from 'app/shared/model/action-change-mail.model';

export const ACTION_TYPES = {
  SEARCH_ACTIONCHANGEMAILS: 'actionChangeMail/SEARCH_ACTIONCHANGEMAILS',
  FETCH_ACTIONCHANGEMAIL_LIST: 'actionChangeMail/FETCH_ACTIONCHANGEMAIL_LIST',
  FETCH_ACTIONCHANGEMAIL: 'actionChangeMail/FETCH_ACTIONCHANGEMAIL',
  CREATE_ACTIONCHANGEMAIL: 'actionChangeMail/CREATE_ACTIONCHANGEMAIL',
  UPDATE_ACTIONCHANGEMAIL: 'actionChangeMail/UPDATE_ACTIONCHANGEMAIL',
  PARTIAL_UPDATE_ACTIONCHANGEMAIL: 'actionChangeMail/PARTIAL_UPDATE_ACTIONCHANGEMAIL',
  DELETE_ACTIONCHANGEMAIL: 'actionChangeMail/DELETE_ACTIONCHANGEMAIL',
  RESET: 'actionChangeMail/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IActionChangeMail>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type ActionChangeMailState = Readonly<typeof initialState>;

// Reducer

export default (state: ActionChangeMailState = initialState, action): ActionChangeMailState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_ACTIONCHANGEMAILS):
    case REQUEST(ACTION_TYPES.FETCH_ACTIONCHANGEMAIL_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ACTIONCHANGEMAIL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_ACTIONCHANGEMAIL):
    case REQUEST(ACTION_TYPES.UPDATE_ACTIONCHANGEMAIL):
    case REQUEST(ACTION_TYPES.DELETE_ACTIONCHANGEMAIL):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_ACTIONCHANGEMAIL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_ACTIONCHANGEMAILS):
    case FAILURE(ACTION_TYPES.FETCH_ACTIONCHANGEMAIL_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ACTIONCHANGEMAIL):
    case FAILURE(ACTION_TYPES.CREATE_ACTIONCHANGEMAIL):
    case FAILURE(ACTION_TYPES.UPDATE_ACTIONCHANGEMAIL):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_ACTIONCHANGEMAIL):
    case FAILURE(ACTION_TYPES.DELETE_ACTIONCHANGEMAIL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_ACTIONCHANGEMAILS):
    case SUCCESS(ACTION_TYPES.FETCH_ACTIONCHANGEMAIL_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_ACTIONCHANGEMAIL):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_ACTIONCHANGEMAIL):
    case SUCCESS(ACTION_TYPES.UPDATE_ACTIONCHANGEMAIL):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_ACTIONCHANGEMAIL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_ACTIONCHANGEMAIL):
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

const apiUrl = 'api/action-change-mails';
const apiSearchUrl = 'api/_search/action-change-mails';

// Actions

export const getSearchEntities: ICrudSearchAction<IActionChangeMail> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_ACTIONCHANGEMAILS,
  payload: axios.get<IActionChangeMail>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IActionChangeMail> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_ACTIONCHANGEMAIL_LIST,
  payload: axios.get<IActionChangeMail>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IActionChangeMail> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ACTIONCHANGEMAIL,
    payload: axios.get<IActionChangeMail>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IActionChangeMail> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ACTIONCHANGEMAIL,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IActionChangeMail> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ACTIONCHANGEMAIL,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IActionChangeMail> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_ACTIONCHANGEMAIL,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IActionChangeMail> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ACTIONCHANGEMAIL,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
